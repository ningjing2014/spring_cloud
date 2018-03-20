package com.ln.xproject.system.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.ln.xproject.base.constants.ValidStatus;
import com.ln.xproject.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user")
public class SysUser extends BaseModel {

    /** 真实姓名 */
    @Column(name = "real_name", nullable = false)
    private String realName;

    /** 邮箱 */
    @Column(name = "email", nullable = false)
    private String email;

    /** 用户状态 */
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ValidStatus status;

    /** 最后一次登录时间 */
    @Column(name = "last_login_time")
    private Date lastLoginTime;

    /** 最后一次登录IP */
    @Column(name = "last_login_ip")
    private String lastLoginIp;

}
