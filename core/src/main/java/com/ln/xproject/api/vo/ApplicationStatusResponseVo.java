package com.ln.xproject.api.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 推标响应Vo
 * 
 * @author taixin
 */

@Data
public class ApplicationStatusResponseVo implements Serializable {

    private static final long serialVersionUID = -4155134405747284298L;

    /** 渠道方借款Id */
    private String loanRefId;

    /** 信审系统进件Id */
    private String applicationId;

    /** 审核状态 */
    private String auditStatus;

    /** 推标状态 */
    private String pushStatus;

    /** 放款状态 */
    private String grantStatus;

    /** 审核时间 */
    private String auditTime;

    /** 推标时间 */
    private String pushTime;

    /** 放款时间 */
    private String grantTime;

    /** 审核拒件原因 */
    private String auditRejectReason;

    /** 推标失败原因 */
    private String pushFailReason;

    /** 放款失败原因 */
    private String grantFailReason;

    /** we返回的code */
    private String weCode;

    /** we返回的message */
    private String weMsg;
}
