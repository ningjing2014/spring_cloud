package com.ln.xproject.api.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 页面进件列表查询请求Vo
 * 
 * @author taixin
 */

@Data
public class ApplicationListRequestVo implements Serializable {

    private static final long serialVersionUID = -3597901087157778898L;

    private String applicationChannel;

    private String auditStatus;

    private String businessType;

    private String userName;

    private String idCard;

    private String mobile;

    private String channelLoanId;

    private BigDecimal minAmount;

    private BigDecimal maxAmount;

    private Long startImportTime;

    private Long endImportTime;

    private Integer pageNum;

    private Integer pageSize;

}
