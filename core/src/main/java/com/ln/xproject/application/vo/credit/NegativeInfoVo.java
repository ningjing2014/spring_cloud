package com.ln.xproject.application.vo.credit;

import java.io.Serializable;

import lombok.Data;
/*
* 央行征信 -- 负面信息
* */

@Data
public class NegativeInfoVo implements Serializable {

    private static final long serialVersionUID = -45400597512667253L;

    /** 已执行 */
    private String alreadyExecute;

    /** 已执行数量 */
    private String alreadyExecuteAmount;

    /** 申请执行 */
    private String applyExecute;

    /** 申请执行数量 */
    private String applyExecuteAmount;

    /** 案件号码 */
    private String caseNo;

    /** 法庭 */
    private String court;

    /** 归档时间 */
    private String filDate;

    /** 逾期时间 */
    private String overDate;

    /** 处理中 */
    private String overWay;

    /** 原因 */
    private String reason;

    /** 报告人ID */
    private String reportId;

    /** 状态 */
    private String status;

}
