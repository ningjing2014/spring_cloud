package com.ln.xproject.system.service;

import java.util.List;

import com.ln.xproject.base.service.BaseService;
import com.ln.xproject.system.constants.PermissionType;
import com.ln.xproject.system.model.SysPermission;
import com.ln.xproject.system.vo.MenuPermVo;
import com.ln.xproject.system.vo.PermTreeVo;
import com.ln.xproject.system.vo.PermsVo;

public interface SysPermissionService extends BaseService<SysPermission> {

    /**
     * 根据用户名查询用户权限字符串列表
     *
     * @param email
     *            用户名
     * @return 用户权限字符串列表
     */
    List<String> findPermissionByEmail(String email);

    /**
     * 获取权限控制信息
     * 
     * @return
     */
    List<PermsVo> findPermsInfo(PermissionType... permissionType);

    /**
     * 获取权限树
     *
     * @return
     */
    List<PermTreeVo> getPermTree();

    /**
     * 加载菜单树列表(菜单和Action权限，不包含管理员权限)
     *
     * @return
     */
    List<PermTreeVo> getAllSysPermission();

    /**
     * 获取角色权限列表
     * 
     * @param roleId
     * @return
     */
    List<Long> getSysPermissionByRole(Long... roleId);

    /**
     * 获取用户权限信息
     * 
     * @param userId
     * @return
     */
    List<MenuPermVo> getSysPermissionByUser(Long userId);

}
