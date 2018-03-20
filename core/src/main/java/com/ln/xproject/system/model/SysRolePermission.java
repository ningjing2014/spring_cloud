package com.ln.xproject.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.ln.xproject.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_role_permission")
public class SysRolePermission extends BaseModel {

    /** 角色 */
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    /** 权限 */
    @Column(name = "permission_id", nullable = false)
    private Long permissionId;

}
