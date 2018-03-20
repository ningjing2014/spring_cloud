package com.ln.xproject.system.vo;

import java.util.Set;

import lombok.Data;

/**
 * Created by ning on 2/13/17.
 */
@Data
public class UserRoleAndChannelInfoVo {

    public UserRoleAndChannelInfoVo(Set<Long> roles, Set<String> channels) {
        this.roles = roles;
        this.channels = channels;
    }

    /** 角色ID */
    Set<Long> roles;
    /** 业务线列表 */
    Set<String> channels;
}
