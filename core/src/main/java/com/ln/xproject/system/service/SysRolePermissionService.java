package com.ln.xproject.system.service;

import java.util.List;
import java.util.Set;

import com.ln.xproject.base.service.BaseService;
import com.ln.xproject.system.model.SysRolePermission;
import com.ln.xproject.system.vo.MenuPermVo;

public interface SysRolePermissionService extends BaseService<SysRolePermission> {

    /**
     * 删除角色对应的权限
     * 
     * @param roleId
     */
    void deleteByRoleId(Long roleId);

    /**
     * 给角色分配权限［删除已有权限，分配新权限,如果权限列表为空，则表示删除角色权限］
     * 
     * @param roleId
     *            角色ID
     * @param permissionIds
     *            权限列表
     */
    void setRolePermission(Long roleId, Set<Long> permissionIds);

    /**
     * 获取角色的菜单加载权限列表
     * 
     * @param roleIds
     * @return
     */
    List<MenuPermVo> getMenuPermsByRoles(Set<Long> roleIds);

}
