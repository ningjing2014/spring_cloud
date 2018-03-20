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
@Table(name = "sys_role")
public class SysRole extends BaseModel {

    /** 角色名称 */
    @Column(name = "name", nullable = false)
    private String name;

    /** 角色描述 */
    @Column(name = "description")
    private String description;

    /** 角色排序 */
    @Column(name = "order_num")
    private Integer orderNum;

}
