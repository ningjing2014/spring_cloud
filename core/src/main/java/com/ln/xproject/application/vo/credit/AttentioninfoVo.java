package com.ln.xproject.application.vo.credit;

import java.io.Serializable;

import lombok.Data;
/*
* 央行征信 -- 关切程度信息
* */

@Data
public class AttentioninfoVo implements Serializable {

    private static final long serialVersionUID = -45400597512667253L;

    /** 近3个月新开所有信贷账户数 */
    private String allActivatedwithin3MCount;

    /** 近3个月新开信用卡账户数 */
    private String creditCardActivatedWithin3MCount;

    /** 近3个月信用卡审批查询次数 */
    private String queryAsCardApplyWithin3MCount;

    /** 近3个月贷款申请查询次数 */
    private String queryAsLoanApplyWithin3MCount;

    /** 近3个月查询次数 */
    private String queryWithin3MCount;

}
