package com.ln.xproject.application.vo.credit;

import java.io.Serializable;

import lombok.Data;
/*
* 央行征信 -- 信用总体信息
* */

@Data
public class CreditInfoVo implements Serializable {

    private static final long serialVersionUID = -45400597512667253L;

    /** 信用卡最近6个月平均额度 */
    private String cardCredit6MAvgPayment;

    /** 信用卡已用额度 */
    private String cardCreditBalance;

    /** 单行最高授信总额 */
    private String cardCreditMax;

    /** 单行最低授信总额 */
    private String cardCreditMin;

    /** 信用卡授信总额 */
    private String cardCreditTotal;

    /** 登记卡账户数 */
    private String creditCardCount;

    /** 信用卡逾期笔数 */
    private String defaultCardCount;

    /** 信用卡单月最高逾期总额 */
    private String defaultCardMaxAmount;

    /** 信用卡最长逾期月数 */
    private String defaultCardMaxDur;

    /** 信用卡逾期月数（次） */
    private String defaultCardMonthCount;

    /** 贷款逾期笔数 */
    private String defaultLoanCount;

    /** 贷款单月最高逾期总额 */
    private String defaultLoanMaxAmount;

    /** 贷款最长逾期月数 */
    private String defaultLoanMaxDur;

    /** 贷款逾期月数（次） */
    private String defaultLoanMonthCount;

    /** 首张贷记卡发卡月份 */
    private String firstCardDate;

    /** 首笔贷款发放月份 */
    private String firstLoanDate;

    /** 住房贷款笔数 */
    private String houseLoanCount;

    /** 6个月平均月还款额 */
    private String osLoan6MAvgPayment;

    /** 未结清贷款余额 */
    private String osLoanBalance;

    /** 未结清贷款总额 */
    private String osLoanTotal;

    /** 其他贷款笔数 */
    private String otherLoanCount;
}
