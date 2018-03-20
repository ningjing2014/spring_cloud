package com.ln.xproject.system.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.Predicate;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.config.CostomPageRequest;
import com.ln.xproject.base.constants.ValidStatus;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.base.vo.PageVo;
import com.ln.xproject.email.service.MailSender;
import com.ln.xproject.kaptcha.KaptchaConstants;
import com.ln.xproject.system.constants.PermissionType;
import com.ln.xproject.system.constants.SearchUserType;
import com.ln.xproject.system.model.SysUser;
import com.ln.xproject.system.model.SysUserSecurity;
import com.ln.xproject.system.repository.SysUserRepository;
import com.ln.xproject.system.service.SysPermissionService;
import com.ln.xproject.system.service.SysUserChannelService;
import com.ln.xproject.system.service.SysUserRoleService;
import com.ln.xproject.system.service.SysUserSecurityService;
import com.ln.xproject.system.service.SysUserService;
import com.ln.xproject.system.vo.ChannelVo;
import com.ln.xproject.system.vo.LoginRstVo;
import com.ln.xproject.system.vo.MenuPermVo;
import com.ln.xproject.system.vo.UserListVo;
import com.ln.xproject.util.CollectionUtils;
import com.ln.xproject.util.PassCheckUtils;
import com.ln.xproject.util.RegexUtil;
import com.ln.xproject.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class SysUserServiceImpl extends BaseServiceImpl<SysUser, SysUserRepository> implements SysUserService {

    public static final String SHIRO_SESSION_KEY = "shiro-kickout-session";
    // 存放用户信息的sessionKey
    public static final String SESSION_USER_INFO = "SESSION_USER_INFO";

    // 存放用户数据权限的Key
    public static final String KEY_DATA_PERMS = "KEY_DATA_PERMS";

    /**
     * session key：是否已被踢出登录，true：已被踢出登录；false、null，""：未被踢出登录
     */
    public static final String SESSION_KEY_KICKOUT = "kickout";

    @Value("${environment.test}")
    private boolean testEnvironment;

    @Autowired
    @Override
    protected void setRepository(SysUserRepository repository) {
        super.repository = repository;
    }

    @Autowired
    private SysUserSecurityService sysUserSecurityService;
    @Autowired
    private CacheManager cacheManager;
    @Autowired
    private DefaultWebSessionManager securityManager;
    @Autowired
    private SysPermissionService sysPermissionService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private SysUserChannelService sysUserChannelService;
    @Autowired
    private MailSender mailSender;

    @Override
    public SysUser getByEmail(String email) {
        return repository.findByEmail(email);
    }

    @Override
    public SysUser loadByEmail(String email) {
        SysUser rcd = repository.findByEmail(email);
        if (rcd == null) {
            throw ExceptionUtils.getServiceException(CodeConstants.C_10111006);
        }

        if (rcd.getStatus() == ValidStatus.DISABLE) {
            throw ExceptionUtils.getServiceException(CodeConstants.C_10111005);
        }
        return rcd;
    }

    @Override
    public SysUser load(Long id) {
        SysUser rcd = super.load(id);
        if (rcd.getStatus() == ValidStatus.DISABLE) {
            throw ExceptionUtils.getServiceException(CodeConstants.C_10111005);
        }
        return rcd;
    }

    @Override
    public LoginRstVo login(String email, String password, String validateCode, String loginIP) {
        Assert.notBlank(email, "邮箱不能为空");
        Assert.notBlank(password, "密码不能为空");
        Assert.notBlank(validateCode, "验证码不能为空");
        // 更新最后一次登录时间
        SysUser user = this.loadByEmail(email);

        SysUserSecurity userSecutity = sysUserSecurityService.findByUserId(user.getId());
        boolean isNeedResetPwd = false;
        // 如果需要改密码，提示改密码
        if (userSecutity.getPasswordNeedReset()) {
            isNeedResetPwd = true;
        }

        // 登录
        Subject subject = SecurityUtils.getSubject();
        if (!testEnvironment) {
            if (!validateCode.equalsIgnoreCase(
                    (String) subject.getSession().getAttribute(KaptchaConstants.KAPTCHA_SESSION_KEY))) {
                log.error("验证码不一致, sessionId:" + subject.getSession().getId() + ", session["
                        + (String) subject.getSession().getAttribute(KaptchaConstants.KAPTCHA_SESSION_KEY) + "], args["
                        + validateCode + "]");
                // 删除session缓存的验证码
                subject.getSession().removeAttribute(KaptchaConstants.KAPTCHA_SESSION_KEY);
                throw ExceptionUtils.getServiceException(CodeConstants.C_10111006);
            }
        }

        // 删除session缓存的验证码
        subject.getSession().removeAttribute(KaptchaConstants.KAPTCHA_SESSION_KEY);
        UsernamePasswordToken token = new UsernamePasswordToken(email, password);
        subject.login(token);
        LoginRstVo rstVo = new LoginRstVo();
        if (subject.isAuthenticated()) {
            // 设置当前登录用户，用于获取当前登录用户
            subject.getSession().setAttribute(SESSION_USER_INFO, user);
            // 设置数据权限
            Set<ChannelVo> channels = this.sysUserChannelService.getChannelByUserId(user.getId());
            Set<String> channelCodes = buildUserChannel(channels);
            subject.getSession().setAttribute(KEY_DATA_PERMS, channelCodes);
            rstVo.setDataPerms(channelCodes);
            // 获取当前登录session信息
            Session session = subject.getSession();
            String username = (String) subject.getPrincipal();
            String sessionId = (String) session.getId();
            Cache<String, String> cache = cacheManager.getCache(SHIRO_SESSION_KEY);

            // 根据名称从Cache中获取session信息(如果有多个登录，此值不为空)
            String cacheSessionId = cache.get(username);
            if (cacheSessionId == null) {
                cacheSessionId = "";
            }
            // 标记用户已被踢出（如果缓存中的sessionId和当前sessionID不同，将cache中的sessionId标记为踢出）
            if (StringUtils.isNotBlank(cacheSessionId) && !cacheSessionId.equals(sessionId)
                    && session.getAttribute(SysUserServiceImpl.SESSION_KEY_KICKOUT) == null) {
                try {
                    Session kickoutSession = securityManager.getSession(new DefaultSessionKey(cacheSessionId));
                    if (kickoutSession != null) {
                        // 设置会话的kickout属性表示踢出了
                        kickoutSession.setAttribute(SysUserServiceImpl.SESSION_KEY_KICKOUT, true);
                    }

                } catch (Exception e) { // 所有异常均可log记录一下即可
                    log.warn("标记用户已被踢出", e);
                }
            }
            // 将当前session放入cache中
            cache.put(username, sessionId);

            // 如果需要改密码，提示改密码
            if (PassCheckUtils.checkPassIsSimple(password)) {
                isNeedResetPwd = true;
            }
        } else {
            throw ExceptionUtils.getServiceException(CodeConstants.C_10111006);
        }
        // 更新用户信息（最后一次登录时间和最后一次登录ip地址）
        user.setLastLoginTime(new Date());
        user.setLastLoginIp(loginIP);
        this.update(user);
        boolean isAdminRole = false;
        if (!isNeedResetPwd) {
            // 获取当前用户的权限信息
            List<MenuPermVo> menuPerms = sysPermissionService.getSysPermissionByUser(user.getId());

            // 如果拥有管理员权限，则直接返回是管理员，否则返回权限列表
            isAdminRole = isAdminRole(menuPerms);
            if (!isAdminRole) {
                rstVo.setMenuPerms(menuPerms);
            }
            rstVo.setIsAdmin(isAdminRole);

        }
        rstVo.setIsNeedResetPwd(isNeedResetPwd);
        rstVo.setEmail(user.getEmail());
        rstVo.setRealName(user.getRealName());
        return rstVo;
    }

    private Set<String> buildUserChannel(Set<ChannelVo> set) {
        Set<String> rst = new HashSet<>();
        set.forEach(rcd -> {
            rst.add(rcd.getCode());
        });
        return rst;
    }

    private boolean isAdminRole(List<MenuPermVo> menuPerms) {
        for (MenuPermVo perm : menuPerms) {
            if (PermissionType.ADMIN.name().equals(perm.getType())) {
                return true;
            }
        }
        return false;
    }

    public void logout() {
        Session session = SecurityUtils.getSubject().getSession(false);
        if (session != null) {
            session.removeAttribute(KaptchaConstants.KAPTCHA_SESSION_KEY);
        }
        SecurityUtils.getSubject().logout();
    }

    @Override
    public void kickOutUser(Long userId) {
        Assert.notNull(userId, "用户主键不能为空");
        SysUser user = this.load(userId);
        Cache<String, String> cache = cacheManager.getCache(SHIRO_SESSION_KEY);
        String cacheSessionId = cache.get(user.getEmail());
        if (StringUtils.isNotBlank(cacheSessionId)) {
            Session kickoutSession = securityManager.getSession(new DefaultSessionKey(cacheSessionId));
            if (kickoutSession != null) {
                // 设置会话的kickout属性表示踢出了
                kickoutSession.setAttribute(SESSION_KEY_KICKOUT, true);
            }
        }

    }

    @Override
    public void addSysUser(String email, String realName) {

        Assert.notBlank(email, "邮箱不能为空");
        Assert.notBlank(realName, "真实姓名不能为空");

        if (!RegexUtil.isEmail(email)) {
            throw ExceptionUtils.commonError("邮箱格式不正确");
        }
        SysUser user = this.getByEmail(email);
        Assert.exist(user, "邮箱");
        SysUser sysUser = new SysUser();
        sysUser.setEmail(email);
        sysUser.setStatus(ValidStatus.ENABLE);
        sysUser.setRealName(realName);
        this.save(sysUser);
        // 初始化用户密码
        String password = this.sysUserSecurityService.initPassword(sysUser.getId());

        if (testEnvironment) {
            log.info(getAddUserEmailContent(realName, email, password));
        }

        // 发邮件
        mailSender.send(new String[] { email }, null, null, "开通财务系统账号",
                getAddUserEmailContent(realName, email, password), null);
    }

    private String getAddUserEmailContent(String realName, String email, String password) {
        StringBuffer sb = new StringBuffer();
        sb.append(realName + " 您好，").append("<br/>").append("您的财务系统登录账号为：").append(email).append(",密码为")
                .append(password).append("。请及时登录修改密码.");
        return sb.toString();

    }

    @Override
    public void updateUserStatus(Long userId, ValidStatus status) {
        Assert.notNull(userId, "用户主键不能为空");
        Assert.notNull(status, "用户状态不能为空");
        SysUser user = this.get(userId);
        if (user == null) {
            throw ExceptionUtils.getServiceException(CodeConstants.C_10111009);
        }
        if (status == user.getStatus()) {
            throw ExceptionUtils.commonError("用户状态变更异常");
        }

        if (ValidStatus.DISABLE == status) {
            // 踢出用户登录
            this.kickOutUser(userId);
            // 删除用户和角色的对应关系
            this.sysUserRoleService.delete(userId);
        }
        user.setStatus(status);
        this.update(user);

    }

    @Override
    public PageVo<UserListVo> searchUserList(SearchUserType type, String value, Integer pageNum, Integer pageSize) {
        CostomPageRequest pageRequest = new CostomPageRequest(pageNum, pageSize, Sort.Direction.DESC, "createTime");

        Specification<SysUser> specification = getSearchSysUserSpecification(type, value);
        Page<SysUser> list = this.repository.findAll(specification, pageRequest);

        if (list == null) {
            return new PageVo<>(null, 0L);
        }
        List<UserListVo> rst = new ArrayList<>();
        list.forEach(rcd -> {
            UserListVo vo = new UserListVo();
            vo.setUserId(rcd.getId());
            vo.setCreateTime(rcd.getCreateTime());
            vo.setRealName(rcd.getRealName());
            vo.setEmail(rcd.getEmail());
            vo.setLastLoginTime(rcd.getLastLoginTime());
            vo.setStatus(rcd.getStatus().name());
            rst.add(vo);
        });

        return new PageVo<>(rst, list.getTotalElements());
    }

    private Specification<SysUser> getSearchSysUserSpecification(SearchUserType type, String value) {
        return (root, query, builder) -> {

            Predicate predicate = builder.conjunction();
            if (type == null || StringUtils.isBlank(value)) {
                return predicate;
            }
            if (type == SearchUserType.EMAIL) {
                predicate.getExpressions().add(builder.like(root.get("email"), value + "%"));
            }

            if (type == SearchUserType.REAL_NAME) {
                predicate.getExpressions().add(builder.like(root.get("realName"), value + "%"));
            }
            return predicate;
        };
    }

    @Override
    public List<Map> getUserByRoleId(Long roleId) {

        Set<String> userIds = sysUserRoleService.getUserByRoleId(roleId);
        if (CollectionUtils.isEmpty(userIds)) {
            return Lists.newArrayList();
        }
        List<Long> ids = userIds.stream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
        List<SysUser> users = this.repository.findAll(ids);
		// 返回关键信息,其他信息忽略
        return users.stream().map(sysUser -> {
            Map map = new HashMap<String,String>();
            map.put("email",sysUser.getEmail());
            map.put("realName",sysUser.getRealName());
            map.put("userId",sysUser.getId());
            return map;
        }).collect(Collectors.toList());
    }

}
