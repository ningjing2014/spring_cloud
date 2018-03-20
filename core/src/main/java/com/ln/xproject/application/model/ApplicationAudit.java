package com.ln.xproject.application.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ln.xproject.application.constants.ApplicationAuditStatus;
import com.ln.xproject.application.constants.ApplicationAuditType;
import com.ln.xproject.base.model.BaseModel;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The persistent class for the application_audit database table.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "application_audit")
public class ApplicationAudit extends BaseModel {

    private static final long serialVersionUID = -1711100496257654888L;

    /** 进件Id */
    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    /** 审核结果 */
    @Column(name = "audit_result", nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicationAuditStatus auditResult;

    /** 审核类型 */
    @Column(name = "audit_type")
    @Enumerated(EnumType.STRING)
    private ApplicationAuditType auditType;

    /** 审核人Id */
    @Column(name = "operator_user_id")
    private Long operatorUserId;

    /** 审核时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "audit_time")
    private Date auditTime;

    /** 拒件原因 */
    @Column(name = "reject_reason")
    private String rejectReason;

}