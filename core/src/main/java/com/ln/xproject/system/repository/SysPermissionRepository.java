package com.ln.xproject.system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.system.constants.PermissionType;
import com.ln.xproject.system.model.SysPermission;

public interface SysPermissionRepository extends BaseRepository<SysPermission> {
    /**
     * 根据用户名查询用户权限字符串列表
     *
     * @param email
     *            用户名
     * @return 用户权限字符串列表
     */
    @Query(value = "  SELECT   p.permission   FROM   sys_user u,   sys_user_role ur,   sys_role_permission rp,"
            + "   sys_permission p   WHERE   u.id = ur.user_id   AND ur.role_id = rp.role_id  "
            + " AND rp.permission_id = p.id   AND u.email = ?1   AND p.permission IS NOT NULL", nativeQuery = true)
    public List<String> findPermissionByEmail(String email);

    /**
     * 根据类型查询权限列表
     * 
     * @param permissionType
     * @return
     */
    public List<SysPermission> findByTypeInOrderByOrderNum(PermissionType... permissionType);

    /**
     * 根据用户名查询用户权限字符串列表
     *
     * @param userId
     *            用户ID
     * @param permissionType
     *            权限类型
     * @return 用户权限字符串列表
     */
    @Query(value = "select p from SysUserRole ur  , SysRolePermission rp, SysPermission p where ur.roleId = rp.roleId "
            + " and rp.permissionId = p.id and ur.userId = :userId and p.type in (:permissionType)")
    public List<SysPermission> findByUserIdAndType(@Param("userId") Long userId,
            @Param("permissionType") PermissionType... permissionType);

}
