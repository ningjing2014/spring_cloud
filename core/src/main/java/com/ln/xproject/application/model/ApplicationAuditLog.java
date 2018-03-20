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

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "application_audit_log")
public class ApplicationAuditLog extends BaseModel {

    private static final long serialVersionUID = -1711100496257654888L;

    /** 审核Id */
    @Column(name = "audit_id", nullable = false)
    private Long auditId;

    /** 进件Id */
    @Column(name = "application_id", nullable = false)
    private Long applicationId;

    /** 操作前审核状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "from_audit_status", nullable = false)
    private ApplicationAuditStatus fromAuditResult;

    /** 操作前审核状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "to_audit_status", nullable = false)
    private ApplicationAuditStatus toAuditResult;

    /** 审核类型 */
    @Enumerated(EnumType.STRING)
    @Column(name = "audit_type", nullable = false)
    private ApplicationAuditType auditType;

    /** 审核人Id */
    @Column(name = "operator_user_id", nullable = false)
    private Long operatorUserId;

    /** 审核时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "audit_time", nullable = false)
    private Date auditTime;

    /** 备注 */
    @Column(name = "remark")
    private String remark;

}