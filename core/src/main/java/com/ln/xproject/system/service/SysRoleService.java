package com.ln.xproject.system.service;

import java.util.List;
import java.util.Set;

import com.ln.xproject.base.service.BaseService;
import com.ln.xproject.system.model.SysRole;
import com.ln.xproject.system.vo.RoleVo;

public interface SysRoleService extends BaseService<SysRole> {

    /**
     * 添加角色
     * 
     * @param name
     * @param description
     * @param orderNum
     */
    void add(String name, String description, Integer orderNum);

    /**
     * 更新角色
     * 
     * @param roleId
     * @param name
     * @param description
     * @param orderNum
     */
    void update(Long roleId, String name, String description, Integer orderNum);

    /**
     * 删除角色
     * 
     * @param roleId
     */
    void delete(Long roleId);

    /**
     * 通过用户id获取用户角色
     * 
     * @param userId
     * @return
     */
    Set<SysRole> getUserRolelist(Long userId);

    /**
     * 删除用户所有角色
     * 
     * @param userId
     */
    void deleteRoleByUserId(Long userId);

    /**
     * 获取角色列表
     * 
     * @return
     */
    List<RoleVo> getRoleList();

}
