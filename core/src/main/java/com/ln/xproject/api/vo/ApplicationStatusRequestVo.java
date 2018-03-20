package com.ln.xproject.api.vo;

import java.io.Serializable;

import com.ln.xproject.base.vo.ApiBaseRequestVo;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 推标请求Vo
 * 
 * @author taixin
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationStatusRequestVo extends ApiBaseRequestVo implements Serializable {

    private static final long serialVersionUID = -7659182601427312186L;

    private String channelLoanId;

}
