package com.ln.xproject.application.vo.credit;

import java.io.Serializable;

import lombok.Data;
/*
* 央行征信 -- 负债信息
* */

@Data
public class LiabilityInfoVo implements Serializable {

    private static final long serialVersionUID = -45400597512667253L;

    /** 在还经营贷款余额 */
    private String businessLoanActiveBalanceSum;

    /** 在还经营贷款月还款额 */
    private String businessLoanActiveDuePaymentSum;

    /** 在还经营贷款总额 */
    private String businessLoanActiveTotalAmountSum;

    /** 在还抵押贷款余额 */
    private String collateralLoanActiveBalanceSum;

    /** 在还抵押贷款月还款额 */
    private String collateralLoanActiveDuePaymentSum;

    /** 在还抵押贷款总额 */
    private String collateralLoanActiveTotalAmountSum;

    /** 未销户信用卡本月实际还款额 */
    private String creditCardActiveActualPaymentSum;

    /** 未销户信用卡透支余额 */
    private String creditCardActiveBalanceSum;

    /** 未销户信用卡月应还金额 */
    private String creditCardActiveDuePaymentSum;

    /** 在还信用贷款余额 */
    private String creditLoanActiveBalanceSum;

    /** 在还信用贷款月还款额 */
    private String creditLoanActiveDuePaymentSum;

    /** 在还信用贷款总额 */
    private String creditLoanActiveTotalAmountSum;

    /** 在还房贷余额 */
    private String housingLoanActiveBalanceSum;

    /** 在还房贷月还款额 */
    private String housingLoanActiveDuePaymentSum;

    /** 在还房贷总额 */
    private String housingLoanActiveTotalAmountSum;

    /** 在还贷款余额 */
    private String loanActiveBalanceSum;

    /** 在还贷款月还款额 */
    private String loanActiveDuePaymentSum;

    /** 在还贷款总额 */
    private String loanActiveTotalAmountSum;

}
