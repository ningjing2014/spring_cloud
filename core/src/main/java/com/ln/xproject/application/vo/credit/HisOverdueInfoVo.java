package com.ln.xproject.application.vo.credit;

import java.io.Serializable;

import lombok.Data;
/*
* 央行征信 -- 历史和逾期信息
* */

@Data
public class HisOverdueInfoVo implements Serializable {

    private static final long serialVersionUID = -45400597512667253L;

    /** 最近6个月所有信贷逾期月数 */
    private String allDefaultMonthWithin6MCount;

    /** 最近6个月所有信贷逾期账户数 */
    private String allDefaultWithin6MCount;

    /** 最早使用信贷距今天数 */
    private String allEffectiveDaysMax;

    /** 首笔经营贷款发放天数 */
    private String businessLoanEffectiveDaysMax;

    /** 首笔抵押贷款发放天数 */
    private String collateralLoanEffectiveDaysMax;

    /** 最近6个月信用卡逾期月数 */
    private String creditCardDefaultMonthWithin6MCount;

    /** 最近6个月信用卡逾期账户数 */
    private String creditCardDefaultWithin6MCount;

    /** 首笔信用贷款发放天数 */
    private String creditLoanEffectiveDaysMax;

    /** 首笔房贷发放天数 */
    private String housingLoanEffectiveDaysMax;

    /** 是否借款人在其他网络借贷平台借款 */
    private String otherPlatLoanSituation;

    /** 最近6个月贷款逾期月数 */
    private String loanDefaultMonthWithin6MCount;

    /** 最近6个月贷款逾期账户数 */
    private String loanDefaultWithin6MCount;

    /** 近5年发生过90天及以上逾期的信用卡数 */
    private String cardCountDf90pIn5Year;

    /** 近5年发生过90天及以上逾期的房贷数 */
    private String housingLoanCountDf90pIn5Year;

    /** 近5年发生过90天及以上逾期的其他贷款数数 */
    private String otherLoanCountDf90pIn5Year;

}
