package com.ln.xproject.system.repository;

import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.system.model.SysUserSecurity;

public interface SysUserSecurityRepository extends BaseRepository<SysUserSecurity> {

    SysUserSecurity findByUserId(Long userId);
}
