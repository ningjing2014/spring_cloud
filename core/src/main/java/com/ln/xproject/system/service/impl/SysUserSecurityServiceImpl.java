package com.ln.xproject.system.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.email.service.MailSender;
import com.ln.xproject.system.model.SysUser;
import com.ln.xproject.system.model.SysUserSecurity;
import com.ln.xproject.system.repository.SysUserSecurityRepository;
import com.ln.xproject.system.service.SysUserSecurityService;
import com.ln.xproject.system.service.SysUserService;
import com.ln.xproject.util.PasswordHelper;
import com.ln.xproject.util.RegexUtil;

@Service
@Transactional
public class SysUserSecurityServiceImpl extends BaseServiceImpl<SysUserSecurity, SysUserSecurityRepository>
        implements SysUserSecurityService {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private MailSender mailSender;

    @Autowired
    @Override
    protected void setRepository(SysUserSecurityRepository repository) {
        super.repository = repository;
    }

    @Override
    public SysUserSecurity findByUserId(Long userId) {
        return this.repository.findByUserId(userId);
    }

    @Override
    public SysUserSecurity loadByUserId(Long userId) {
        SysUserSecurity sus = this.findByUserId(userId);
        Assert.notExist(sus, "密码信息");
        return sus;
    }

    public void resetPassword(String email, String realName) {
        if (!RegexUtil.isEmail(email)) {
            throw ExceptionUtils.commonError("邮箱格式错误");
        }
        SysUser user = this.sysUserService.loadByEmail(email);
        if (!user.getRealName().equals(realName)) {
            throw ExceptionUtils.commonError("姓名与邮箱不符");
        }
        String newPassword = this.initPassword(user.getId());
        mailSender.send(new String[] { email }, null, null, "找回密码",
                getForgerUserEmailContent(realName, email, newPassword), null);
    }

    private String getForgerUserEmailContent(String realName, String email, String password) {
        StringBuffer sb = new StringBuffer();
        sb.append(realName + " 您好，").append("<br/>").append("您的财务系统密码为").append(password).append("。请及时登录修改密码.");
        return sb.toString();

    }

    @Override
    public SysUser updatePassword(Long userId, String password, String newPassword) {
        Assert.notNull(userId, "用户不能为空");
        Assert.notBlank(password, "密码不能为空");
        Assert.notBlank(newPassword, "新密码不能为空");

        SysUser user = sysUserService.load(userId);

        SysUserSecurity userSecutity = loadByUserId(user.getId());

        // 校验用户密码是否正确
        if (!userSecutity.getPassword()
                .equals(PasswordHelper.encryptPassword(password, userSecutity.getPasswordSalt()))) {
            throw ExceptionUtils.getServiceException(CodeConstants.C_10111013);
        }
        // 校验新密码的格式是否正确
        if (!RegexUtil.isValidPassword(newPassword)) { // 输入密码不符合规则
            throw ExceptionUtils.commonError("请输入8-16位含半角、英文、数字、特殊符号（！，@，#，$，%，^，&,*,.,_）组成的密码。");
        }

        // 2.修改密码
        userSecutity = initPassword(userSecutity.getUserId(), newPassword);
        userSecutity.setPasswordNeedReset(false);
        this.update(userSecutity);

        return user;
    }

    @Override
    public String initPassword(Long userId) {
        Assert.notNull(userId, "用户主键不能为空");
        // 生成随机密码
        String password = PasswordHelper.generateNumberAndLetterValidCode(6);
        SysUserSecurity userSecutity = initPassword(userId, password);
        userSecutity.setPasswordNeedReset(true);
        this.save(userSecutity);
        return password;
    }

    @Override
    public String resetPassword(Long userId) {
        Assert.notNull(userId, "用户主键不能为空");
        sysUserService.load(userId);
        String password = PasswordHelper.generateNumberAndLetterValidCode(6);
        SysUserSecurity userSecutity = initPassword(userId, password);
        userSecutity.setPasswordNeedReset(true);
        this.save(userSecutity);
        return password;
    }

    /**
     * 设置密码和盐
     * 
     * @param userId
     * @param password
     * @return
     */
    private SysUserSecurity initPassword(Long userId, String password) {
        Assert.notNull(userId, "用户主键不能为空");
        Assert.notBlank(password, "密码不能为空");
        sysUserService.load(userId);
        SysUserSecurity userSecutity = this.findByUserId(userId);
        if (userSecutity == null) {
            userSecutity = new SysUserSecurity();
            userSecutity.setUserId(userId);
        }

        userSecutity.setPassword(password);
        // 加密并设置盐
        PasswordHelper.encryptPassword(userSecutity);
        return userSecutity;
    }

}
