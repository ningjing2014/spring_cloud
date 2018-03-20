package com.ln.xproject.application.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.ln.xproject.application.constants.ApplicationAuditStatus;
import com.ln.xproject.application.constants.ApplicationChannel;
import com.ln.xproject.application.constants.ApplicationPushStatus;
import com.ln.xproject.application.constants.NoticeStatusEnum;
import com.ln.xproject.application.constants.PayStatus;
import com.ln.xproject.base.model.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 进件
 *
 * @author taixin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "application")
public class Application extends BaseModel {

    private static final long serialVersionUID = -8673965995555745497L;

    /** 用户Id */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /** 进件渠道 */
    @Enumerated(EnumType.STRING)
    @Column(name = "application_channel", nullable = false)
    private ApplicationChannel applicationChannel;

    /** 渠道方借款Id */
    @Column(name = "channel_loan_id", nullable = false)
    private String channelLoanId;

    /** 进件审核状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "audit_status", nullable = false)
    private ApplicationAuditStatus auditStatus;

    /** 进件推标状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "push_status", nullable = false)
    private ApplicationPushStatus pushStatus;

    /** 通知状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "notify_status", nullable = false)
    private NoticeStatusEnum notityStatus;

    /** 放款状态 */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_confirm_status", nullable = false)
    private PayStatus paymentConfirmStatus;

    /** 审核时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "audit_time")
    private Date auditTime;

    /** 推标成功时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "push_time")
    private Date pushTime;

    /** 通知时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "notify_time")
    private Date notifyTime;

    /** 放款成功时间 */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "payment_confirm_time")
    private Date paymentConfirmTime;

    /** 是否初始化完成 */
    @Column(name = "is_inited", nullable = false)
    private Boolean isInited;

    /** 生成进件号 */
    @Column(name = "verify_serial_no", nullable = false)
    private String verifySerialNo;

    /** 借款标题 */
    @Column(name = "title", nullable = false)
    private String title;

    /** 借款用途 */
    @Column(name = "borrow_type", nullable = false)
    private String borrowType;

    /** 借款金额 */
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    /** 借款期限 */
    @Column(name = "periods", nullable = false)
    private Integer periods;

    /** 借款利率 */
    @Column(name = "interest", nullable = false)
    private BigDecimal interest;

    /** 描述 */
    @Column(name = "description", nullable = false)
    private String description;

    /** 合同编号 */
    @Column(name = "contract_code")
    private String contractCode;

    /** 服务费 */
    @Column(name = "service_fee", nullable = false)
    private BigDecimal serviceFee;

    /** 共债Id */
    @Column(name = "ticket_id", nullable = false)
    private String ticketId;

    /** 通知URL */
    @Column(name = "notify_url", nullable = false)
    private String notifyUrl;

    /** 门店 */
    @Column(name = "shop")
    private String shop;

    /** 销售机构Id */
    @Column(name = "group_id", nullable = false)
    private String groupId;

    /** 申请城市 */
    @Column(name = "area_group_id", nullable = false)
    private String areaGroupId;

    /** 大区id */
    @Column(name = "district_group_id", nullable = false)
    private String districtGroupId;

    /** 实际年化利率 */
    @Column(name = "internal_interest_rate")
    private BigDecimal internalInterestRate;

    /** 实际月还款额 */
    @Column(name = "internal_repay_amount")
    private BigDecimal internalRepayAmount;

    /** 审核备注 */
    @Column(name = "notes")
    private String notes;

    /** 客服名称 */
    @Column(name = "staff_name")
    private String staffName;

    /** 支付流水号 */
    @Column(name = "pay_serial_no")
    private String paySerialNo;

    /** we理财借款Id */
    @Column(name = "we_loan_id")
    private String weLoanId;

    /** 请求we的Code */
    @Column(name = "we_code")
    private String weCode;

    /** 请求we的相应消息 */
    @Column(name = "we_msg")
    private String weMsg;

    /** 失败原因 */
    @Column(name = "fail_reason")
    private String failReason;

    /** 是否新流程 */
    @Column(name = "new_flow")
    private String newFlow;

}