package com.ln.xproject.system.repository;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.system.model.SysRole;

public interface SysRoleRepository extends BaseRepository<SysRole> {

    @Query("select sr from SysRole sr where sr.id in ( select sur.roleId from  SysUserRole sur where sur.userId = :userId )")
    Set<SysRole> getUserRolelist(@Param("userId") Long userId);

    @Modifying
    @Transactional
    @Query("delete from SysUserRole sur where sur.userId = :userId ")
    void deleteRoleByUserId(@Param("userId") Long userId);
}
