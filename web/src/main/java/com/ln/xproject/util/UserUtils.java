package com.ln.xproject.util;

import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;

import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.system.model.SysUser;
import com.ln.xproject.system.service.impl.SysUserServiceImpl;

/**
 * Created by ning on 1/20/17.
 */
public class UserUtils {

    /**
     * 获取当前登录用户
     * 
     * @return
     */
    public static SysUser getCurrentUser() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            throw ExceptionUtils.getServiceException(CodeConstants.C_10111000);
        }
        Assert.notNull(subject, "获取当前登录用户异常");
        Session session = subject.getSession();
        Assert.notNull(session, "获取当前登录用户异常");
        SysUser sysUser = (SysUser) session.getAttribute(SysUserServiceImpl.SESSION_USER_INFO);
        return sysUser;

    }

    /**
     * 获取当前登录用户
     *
     * @return
     */
    public static Long getCurrentUserId() {
        SysUser sysUser = getCurrentUser();
        return sysUser != null ? sysUser.getId() : null;
    }

    /**
     * 获得当前用户名
     *
     * @return
     */
    public static String getCurrentUsername() {
        Subject subject = SecurityUtils.getSubject();
        PrincipalCollection collection = subject.getPrincipals();
        if (null != collection && !collection.isEmpty()) {
            return (String) collection.iterator().next();
        }
        return null;
    }

    /**
     * 获取当前登录用户数据权限列表
     *
     * @return
     */
    public static Set<String> getDataPerms() {
        Subject subject = SecurityUtils.getSubject();
        if (!subject.isAuthenticated()) {
            throw ExceptionUtils.getServiceException(CodeConstants.C_10111000);
        }
        Assert.notNull(subject, "获取当前登录用户异常");
        Session session = subject.getSession();
        Assert.notNull(session, "获取当前登录用户异常");
        Set<String> dataPerms = (Set<String>) session.getAttribute(SysUserServiceImpl.KEY_DATA_PERMS);
        return dataPerms;

    }

}
