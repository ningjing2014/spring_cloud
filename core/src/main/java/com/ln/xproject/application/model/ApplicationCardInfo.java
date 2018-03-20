package com.ln.xproject.application.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ln.xproject.base.model.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the application_card_info database table.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "application_card_info")
public class ApplicationCardInfo extends BaseModel {
    private static final long serialVersionUID = 1L;

    /** 进件Id */
    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    /** 账户户名 */
    @Column(name = "user_name", nullable = false)
    private String userName;

    /** 银行卡号 */
    @Column(name = "bank_card_no", nullable = false)
    private String bankCardNo;

    /** 银行代码 */
    @Column(name = "bank_code")
    private String bankCode;

    /** 银行名称 */
    @Column(name = "bank_name", nullable = false)
    private String bankName;

    /** 支行名称 */
    @Column(name = "bank_address", nullable = false)
    private String bankAddress;

    /** 开户省份 */
    @Column(name = "province", nullable = false)
    private String province;

    /** 开户城市 */
    @Column(name = "city", nullable = false)
    private String city;

    /** 银行大额支付行号 */
    @Column(name = "bank_cde", nullable = false)
    private String bankCde;

    /** 添加时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "add_time")
    private Date addTime;
}