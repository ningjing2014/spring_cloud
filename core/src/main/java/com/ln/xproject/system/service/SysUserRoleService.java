package com.ln.xproject.system.service;

import java.util.List;
import java.util.Set;

import com.ln.xproject.base.service.BaseService;
import com.ln.xproject.system.model.SysRole;
import com.ln.xproject.system.model.SysUserRole;
import com.ln.xproject.system.vo.UserRoleAndChannelInfoVo;

public interface SysUserRoleService extends BaseService<SysUserRole> {

    /**
     * 根据用户名查询用户角色名称列表
     *
     * @param email
     *            登录邮件
     * @return 用户角色名称列表
     */
    List<String> findRoleNamesByEmail(String email);

    /**
     * 根据角色ID查询该角色分配给了哪些用户
     * 
     * @param roleId
     * @return
     */
    List<SysUserRole> findByRoleId(Long roleId);

    /**
     * 删除角色及角色和权限关系
     * 
     * @param roleId
     */
    void delete(Long roleId);

    /**
     * 删除某用户的某些角色
     * 
     * @param userId
     * @param roleIds
     */
    void delete(Long userId, Long... roleIds);

    /**
     * 给用户的某些角色
     *
     * @param userId
     * @param roleIds
     */
    void add(Long userId, Long... roleIds);

    /**
     * 获取用户的角色列表
     * 
     * @param userId
     * @return
     */
    Set<SysRole> getUserRolelist(Long userId);

    /**
     * 获取用户角色和业务线信息
     * 
     * @param userId
     * @return
     */
    UserRoleAndChannelInfoVo getUserRoleAndChannelInfo(Long userId);

    /**
     * 给用户分配角色
     * 
     * @param userId
     * @param roleId
     */
    void setUserRoles(Long userId, Long... roleId);

    /**
     * 设置用户角色和业务线信息
     * 
     * @param userId
     * @param roleId
     * @param channels
     */
    void setUserRolesAndChannelInfo(Long userId, Long[] roleId, String[] channels);

    Set<String> getUserByRoleId(Long roleId);
}
