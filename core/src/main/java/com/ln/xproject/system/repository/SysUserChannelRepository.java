package com.ln.xproject.system.repository;

import java.util.Set;

import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.system.model.SysUserChannel;

public interface SysUserChannelRepository extends BaseRepository<SysUserChannel> {

    Set<SysUserChannel> findByUserId(Long userId);

    void deleteByUserId(Long userId);
}
