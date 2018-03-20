package com.ln.xproject.api.vo;

import java.io.Serializable;

import lombok.Data;

/**
 * 推标响应Vo
 * 
 * @author taixin
 */

@Data
public class ApplicationImportResponseVo implements Serializable {

    private static final long serialVersionUID = 33395623627963631L;

    /** 渠道方借款Id */
    private String loanRefId;

    /** 信审系统进件Id */
    private String applicationId;
}
