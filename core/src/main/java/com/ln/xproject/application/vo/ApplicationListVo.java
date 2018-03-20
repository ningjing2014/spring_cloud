package com.ln.xproject.application.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class ApplicationListVo implements Serializable {

    private static final long serialVersionUID = -45400597512667253L;

    /** 信审系统进件Id */
    private String applicationId;

    /** 渠道方借款Id */
    private String channelLoanId;

    /** 用户姓名 */
    private String userName;

    /** 用户手机号 */
    private String mobile;

    /** 用户身份证号 */
    private String idCard;

    /** 借款金额 */
    private String amount;

    /** 审核状态 */
    private String auditStatus;

    /** 审核状态中文表述 */
    private String auditStatusDesc;

    /** 推标状态 */
    private String pushStatus;

    /** 推标状态中文表述 */
    private String pushStatusDesc;

    /** 审核人员 */
    private String operator;

    /** 进件时间 */
    private String importTime;

    /** 产品名称 */
    private String productName;

}
