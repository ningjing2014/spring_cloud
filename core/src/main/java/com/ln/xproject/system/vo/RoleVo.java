package com.ln.xproject.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by ning on 2/14/17.
 */

@Data
@AllArgsConstructor
public class RoleVo {
    /** 角色ID */
    private Long roleId;
    /** 角色名称 */
    private String roleName;
}
