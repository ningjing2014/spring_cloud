package com.ln.xproject.application.vo;

import java.io.Serializable;

import com.ln.xproject.base.exception.Assert;

import lombok.Data;

@Data
public class ApplicationCardImportVo implements Serializable {

    private static final long serialVersionUID = 7073637948036221096L;

    /** 账户户名 */
    private String userName;

    /** 银行卡号 */
    private String bankCardNo;

    /** 银行代码 */
    private String bankCode;

    /** 银行名称 */
    private String bankName;

    /** 支行名称 */
    private String bankAddress;

    /** 开户省份 */
    private String province;

    /** 开户城市 */
    private String city;

    /** 银行大额支付行号 */
    private String bankCde;

    /** 添加时间 */
    private Long addTime;

    public void checkImport() {
        Assert.notBlank(userName, "userName");
        Assert.notBlank(bankCardNo, "bankCardNo");
        Assert.notBlank(bankName, "bankName");
        Assert.notBlank(bankAddress, "bankAddress");
        Assert.notBlank(province, "province");
        Assert.notBlank(city, "city");
        Assert.notBlank(bankCde, "bankCde");
    }
}
