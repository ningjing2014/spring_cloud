package com.ln.xproject.system.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.system.model.SysRole;
import com.ln.xproject.system.model.SysUserRole;
import com.ln.xproject.system.repository.SysUserRoleRepository;
import com.ln.xproject.system.service.SysRoleService;
import com.ln.xproject.system.service.SysUserChannelService;
import com.ln.xproject.system.service.SysUserRoleService;
import com.ln.xproject.system.service.SysUserService;
import com.ln.xproject.system.vo.ChannelVo;
import com.ln.xproject.system.vo.UserRoleAndChannelInfoVo;
import com.ln.xproject.util.CollectionUtils;

@Service
@Transactional
public class SysUserRoleServiceImpl extends BaseServiceImpl<SysUserRole, SysUserRoleRepository>
        implements SysUserRoleService {

    @Autowired
    @Override
    protected void setRepository(SysUserRoleRepository repository) {
        super.repository = repository;
    }

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysUserChannelService sysUserChannelService;

    @Override
    public List<String> findRoleNamesByEmail(String email) {
        return repository.findRoleNamesByEmail(email);
    }

    @Override
    public List<SysUserRole> findByRoleId(Long roleId) {
        return repository.findByRoleId(roleId);
    }

    @Override
    public void delete(Long roleId) {
        repository.deleteByRoleId(roleId);
    }

    @Override
    public void delete(Long userId, Long... roleIds) {
        sysUserService.load(userId);
        if (roleIds == null || roleIds.length == 0) {
            this.repository.deleteByUserId(userId);
        } else {
            for (int i = 0; i < roleIds.length; i++) {
                Long roleId = roleIds[i];
                sysRoleService.load(roleId);
                this.repository.deleteByUserIdAndRoleId(userId, roleId);
            }
        }
    }

    @Override
    public void add(Long userId, Long... roleIds) {
        if (roleIds == null || roleIds.length == 0) {
            throw ExceptionUtils.getServiceException(CodeConstants.C_10111011);
        }
        sysUserService.load(userId);
        for (int i = 0; i < roleIds.length; i++) {
            Long roleId = roleIds[i];
            sysRoleService.load(roleId);
            SysUserRole sysUserRole = new SysUserRole();
            sysUserRole.setUserId(userId);
            sysUserRole.setRoleId(roleId);
            this.save(sysUserRole);
        }
    }

    private boolean hasRole(Set<SysRole> sysRoleList, Long roleId) {
        for (SysRole sysRole : sysRoleList) {
            if (sysRole.getId() == roleId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Set<SysRole> getUserRolelist(Long userId) {
        return sysRoleService.getUserRolelist(userId);
    }

    @Override
    public UserRoleAndChannelInfoVo getUserRoleAndChannelInfo(Long userId) {
        // 获取用户角色列表
        Set<SysRole> roles = this.getUserRolelist(userId);
        // 获取用户业务线列表
        Set<ChannelVo> busLines = sysUserChannelService.getChannelByUserId(userId);

        return new UserRoleAndChannelInfoVo(buildUserRoleIds(roles), buildUserChannel(busLines));
    }

    private Set<Long> buildUserRoleIds(Set<SysRole> roles) {
        Set<Long> rst = new HashSet<>();
        roles.forEach(role -> {
            rst.add(role.getId());
        });
        return rst;
    }

    private Set<String> buildUserChannel(Set<ChannelVo> channels) {
        Set<String> rst = new HashSet<>();
        channels.forEach(channel -> {
            rst.add(channel.getCode());
        });
        return rst;
    }

    @Override
    public void setUserRoles(Long userId, Long... roleId) {
        sysRoleService.deleteRoleByUserId(userId);
        add(userId, roleId);
    }

    @Override
    public void setUserRolesAndChannelInfo(Long userId, Long[] roleId, String[] businessChannel) {
        // 设置用户角色
        this.setUserRoles(userId, roleId);
        // 设置用户业务线信息
        Set<String> channels = new HashSet<>();
        if (businessChannel != null && businessChannel.length > 0) {
            CollectionUtils.addAll(channels, businessChannel);
        }
        sysUserChannelService.setUserChannel(userId, channels);
    }

    @Override
    public Set<String> getUserByRoleId(Long roleId) {
        List<SysUserRole> list = this.findByRoleId(roleId);
        Set<String> set = list.stream().map(sysUserRole -> sysUserRole.getUserId().toString())
                .collect(Collectors.toSet());
        return set;
    }

}
