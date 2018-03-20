package com.ln.xproject.api.vo;

import java.io.Serializable;

import com.ln.xproject.application.vo.ApplicationCardImportVo;
import com.ln.xproject.application.vo.ApplicationImportVo;
import com.ln.xproject.application.vo.ApplicationUserImportVo;
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
public class ApplicationImportRequestVo extends ApiBaseRequestVo implements Serializable {

    private static final long serialVersionUID = 3527481571800026955L;

    private ApplicationImportVo loan;

    private ApplicationUserImportVo user;

    private ApplicationCardImportVo bank;
}
