package com.ln.xproject.shiro;

import java.util.HashSet;

import javax.annotation.Resource;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;

import com.ln.xproject.base.constants.ValidStatus;
import com.ln.xproject.system.model.SysUser;
import com.ln.xproject.system.model.SysUserSecurity;
import com.ln.xproject.system.repository.SysPermissionRepository;
import com.ln.xproject.system.repository.SysUserRepository;
import com.ln.xproject.system.repository.SysUserRoleRepository;
import com.ln.xproject.system.repository.SysUserSecurityRepository;

public class UserRealm extends AuthorizingRealm {

    @Resource
    private SysUserRoleRepository sysUserRoleRepository;
    @Resource
    private SysPermissionRepository sysPermissionRepository;
    @Resource
    private SysUserRepository sysUserRepository;
    @Resource
    private SysUserSecurityRepository sysUserSecurityRepository;

    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        String username = (String) principalCollection.getPrimaryPrincipal();

        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();

        authorizationInfo
                .setStringPermissions(new HashSet<String>(sysPermissionRepository.findPermissionByEmail(username)));
        return authorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken)
            throws AuthenticationException {

        UsernamePasswordToken userTypeUsernamePasswordToken = (UsernamePasswordToken) authenticationToken;
        String username = (String) userTypeUsernamePasswordToken.getPrincipal();

        SysUser user = sysUserRepository.findByEmail(username);
        if (user == null) { // 没找到帐号
            throw new UnknownAccountException();
        }

        if (user.getStatus() == ValidStatus.DISABLE) { // 用户已删除
            throw new DisabledAccountException();
        }

        // 查询用户密码信息
        SysUserSecurity userSecutity = sysUserSecurityRepository.findByUserId(user.getId());

        // 交给AuthenticatingRealm使用CredentialsMatcher进行密码匹配
        SimpleAuthenticationInfo authenticationInfo = new SimpleAuthenticationInfo(user.getEmail(), // 用户名
                userSecutity.getPassword(), // 密码
                ByteSource.Util.bytes(userSecutity.getPasswordSalt()), // 密码盐
                getName() // realm name
        );
        return authenticationInfo;
    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

}
