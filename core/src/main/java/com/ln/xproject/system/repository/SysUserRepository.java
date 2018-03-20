package com.ln.xproject.system.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.system.model.SysUser;

public interface SysUserRepository extends BaseRepository<SysUser>, JpaSpecificationExecutor<SysUser> {
    SysUser findByEmail(String email);

    @Query(value = "select p.permission " + "from sys_user u " + "inner join sys_user_role ur on u.id = ur.user_id "
            + "inner join sys_role r on ur.role_id = r.id " + "inner join sys_role_permission rp on r.id = rp.role_id "
            + "inner join sys_permission p on rp.permission_id = p.id "
            + "where u.username = ?1 and p.url=?2 ", nativeQuery = true)

    public List<String> findPermissionStrs(String username, String htmlUrl);

    @Query("select u from SysUser u where  u.realName like '?1%'")
    Page<SysUser> searchByRealName(@Param("realName") String realName, Pageable pageable);

    @Query("select u from SysUser u where  u.email like '?1%'")
    Page<SysUser> searchByEmail(@Param("email") String email, Pageable pageable);
}
