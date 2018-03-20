package com.ln.xproject.system.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

import com.ln.xproject.base.model.BaseModel;

import com.ln.xproject.system.constants.ChannelType;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "sys_user_channel")
public class SysUserChannel extends BaseModel {

    /** 用户主键 */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    /** 业务线 */
    @Column(name = "channel", nullable = false)
    @Enumerated(EnumType.STRING)
    private ChannelType channel;

}
