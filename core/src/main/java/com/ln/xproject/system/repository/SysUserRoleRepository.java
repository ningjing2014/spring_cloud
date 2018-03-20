package com.ln.xproject.system.repository;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.system.model.SysUserRole;

public interface SysUserRoleRepository extends BaseRepository<SysUserRole> {

    @Query(value = "select r.name " + "from sys_user u " + "inner join sys_user_role ur on u.id = ur.user_id "
            + "inner join sys_role r on ur.role_id = r.id " + "where u.email = ?1", nativeQuery = true)

    public List<String> findRoleNamesByEmail(String email);

    public List<SysUserRole> findByRoleId(Long roleId);

    public List<SysUserRole> findByUserId(Long userId);

    @Modifying
    @Transactional
    public void deleteByUserIdAndRoleId(Long userId, Long roleId);

    public void deleteByRoleId(Long roleId);

    public void deleteByUserId(Long userId);

}
