package com.ln.xproject.application.vo.credit;

import java.io.Serializable;

import lombok.Data;
/*
* 央行征信 -- 担保信息
* */

@Data
public class GuaranteeInfoVo implements Serializable {

    private static final long serialVersionUID = -45400597512667253L;

    /** 银行 */
    private String bankName;

    /** 卡号 */
    private String cardNum;

    /** 类型 */
    private String cardType;

    /** 担保时间 */
    private String dealDate;

    /** 为担保人 */
    private String forPersonName;

    /** 担保金额 */
    private String guaranteeAmount;

    /** 担保余额 */
    private String guaranteeBalance;

    /** 担保合同金额 */
    private String guaranteeContractAmount;

    /** 担保类型 */
    private String guaranteeType;

    /** 信息时间 */
    private String infoDate;

}
