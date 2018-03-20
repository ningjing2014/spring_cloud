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
 * The persistent class for the application_audit database table.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "credit_report")
public class CreditReport extends BaseModel {

    private static final long serialVersionUID = -1711100496257654888L;

    /** 进件Id */
    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    /** 证件类型 */
    @Column(name = "id_type", nullable = false)
    private String idType;

    /** 证件号码 */
    @Column(name = "id_no", nullable = false)
    private String idNo;

    /** 客户姓名 */
    @Column(name = "cust_name", nullable = false)
    private String custName;

    /** 客户性别 */
    @Column(name = "gender", nullable = false)
    private String gender;

    /** 查询时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "search_time", nullable = false)
    private Date searchTime;

    /** 报告时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "report_time", nullable = false)
    private Date reportTime;

    /** 担保信息 */
    @Column(name = "guarantee_info")
    private String guaranteeInfo;

    /** 负面信息 */
    @Column(name = "negative_info")
    private String negativeInfo;

    /** 信用总体信息 */
    @Column(name = "credit_info")
    private String creditInfo;

    /** 历史和逾期信息 */
    @Column(name = "his_overdue")
    private String hisOverdue;

    /** 负债信息 */
    @Column(name = "liability_info")
    private String liabilityInfo;

    /** 关切程度信息 */
    @Column(name = "attention_info")
    private String attentionInfo;

}