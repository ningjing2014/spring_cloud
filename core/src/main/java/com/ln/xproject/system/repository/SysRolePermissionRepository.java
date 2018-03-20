package com.ln.xproject.system.repository;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.Query;

import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.system.model.SysPermission;
import com.ln.xproject.system.model.SysRolePermission;

public interface SysRolePermissionRepository extends BaseRepository<SysRolePermission> {

    @Query("select distinct sp from SysRolePermission srp,SysPermission sp where srp.permissionId = sp.id and  srp.roleId in (?1 )")
    List<SysPermission> findPermissionByRoleIds(Set<Long> roleIds);

    void deleteByRoleId(Long roleId);

}
