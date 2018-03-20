package com.ln.xproject.system.service;

import com.ln.xproject.base.service.BaseService;
import com.ln.xproject.system.model.SysUser;
import com.ln.xproject.system.model.SysUserSecurity;

public interface SysUserSecurityService extends BaseService<SysUserSecurity> {

    /**
     * 获取用户密码信息
     * 
     * @param userId
     * @return
     */
    SysUserSecurity findByUserId(Long userId);

    /**
     * 获取用户密码信息
     * 
     * @param userId
     * @return
     */
    SysUserSecurity loadByUserId(Long userId);

    /**
     * @param userId
     *            用户主键
     * @param password
     *            原密码
     * @param newPassword
     *            新密码
     * @return
     */
    SysUser updatePassword(Long userId, String password, String newPassword);

    /**
     * 初始化用户密码
     * 
     * @param userId
     * @return 新密码
     */
    String initPassword(Long userId);

    /**
     * 重置密码
     * 
     * @param userId
     */
    String resetPassword(Long userId);

    /**
     * 重置登录密码（密码以邮件方式发送）
     * 
     * @param email
     * @param realName
     */
    void resetPassword(String email, String realName);

}
