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
@Table(name = "sys_user_role")
public class SysUserRole extends BaseModel {

    /** 角色 */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 权限 */
    @Column(name = "role_id", nullable = false)
    private Long roleId;
}
