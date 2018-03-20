package com.ln.xproject.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.ln.xproject.base.model.BaseModel;
import com.ln.xproject.system.constants.PermissionType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_permission")
public class SysPermission extends BaseModel {

    /** 权限名称 */
    @Column(name = "name", nullable = false)
    private String name;

    /** url地址 */
    @Column(name = "url")
    private String url;

    /** 权限字符串,如：user:view */
    @Column(name = "permission", nullable = false)
    private String permission;

    /** 权限描述 */
    @Column(name = "description")
    private String description;

    /** 权限类型 */
    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionType type;

    /** 权限排序 */
    @Column(name = "order_num")
    private Integer orderNum;

    /** 父级权限 */
    @Column(name = "parent_id")
    private Long parentId;

}
