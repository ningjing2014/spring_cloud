package com.ln.xproject.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.ln.xproject.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by ning on 1/18/17.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user_security")
public class SysUserSecurity extends BaseModel {
    /** 用户Id */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 密码 */
    @Column(name = "password", nullable = false)
    private String password;

    /** 密码盐 */
    @Column(name = "password_salt", nullable = false)
    private String passwordSalt;

    /** 是否需要重设密码 */
    @Column(name = "password_need_reset", nullable = false)
    private Boolean passwordNeedReset;

}
