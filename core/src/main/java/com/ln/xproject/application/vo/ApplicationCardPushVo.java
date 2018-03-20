package com.ln.xproject.application.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class ApplicationCardPushVo implements Serializable {

    private static final long serialVersionUID = -1334355097212970182L;

    /** 账户户名 */
    private String userName;

    /** 银行卡号 */
    private String bankCardNo;

    /** 银行名称 */
    private String bankName;

    /** 支行名称 */
    private String bankAddress;

    /** 开户省份 */
    private String province;

    /** 开户城市 */
    private String city;

}
