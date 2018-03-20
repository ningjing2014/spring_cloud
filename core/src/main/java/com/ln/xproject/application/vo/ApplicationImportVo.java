package com.ln.xproject.application.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import com.ln.xproject.base.exception.Assert;

import lombok.Data;

@Data
public class ApplicationImportVo implements Serializable {

    private static final long serialVersionUID = -45400597512667253L;

    /** 渠道方借款Id */
    private String channelLoanId;

    /** 借款标题 */
    private String title;

    /** 借款用途 */
    private String borrowType;

    /** 借款金额 */
    private BigDecimal amount;

    /** 借款期限 */
    private Integer periods;

    /** 借款利率 */
    private BigDecimal interest;

    /** 描述 */
    private String description;

    /** 合同编号 */
    private String contractCode;

    /** 服务费 */
    private BigDecimal serviceFee;

    /** 共债Id */
    private String ticketId;

    /** 通知URL */
    private String notifyUrl;

    /** 门店 */
    private String shop;

    /** 销售机构Id */
    private String groupId;

    /** 申请城市 */
    private String areaGroupId;

    /** 大区id */
    private String districtGroupId;

    /** 实际年化利率 */
    private BigDecimal internalInterestRate;

    /** 实际年化利率 */
    private BigDecimal internalRepayAmount;

    /** 审核备注 */
    private String notes;

    /** 客服名称 */
    private String staffName;

    /** 是否新流程 */
    private String newFlow;

    public void checkImport() {
        Assert.notBlank(channelLoanId, "channelLoanId");
        Assert.notBlank(title, "title");
        Assert.notBlank(borrowType, "borrowType");
        Assert.notBlank(description, "description");
        Assert.gtZero(amount, "amount");
        Assert.gtZero(periods, "periods");
        Assert.gtZero(interest, "interest");
        Assert.geZero(serviceFee, "serviceFee");
        Assert.notBlank(ticketId, "ticketId");
        Assert.notBlank(notifyUrl, "notifyUrl");
        Assert.notBlank(groupId, "groupId");
        Assert.notBlank(areaGroupId, "areaGroupId");
        Assert.notBlank(districtGroupId, "districtGroupId");
    }
}
