package com.ln.xproject.base.vo;

import lombok.Data;

/**
 * Created by ning on 9/21/17.
 */
@Data
public class ApiBaseRequestVo {

    /** 版本 */
    protected String serviceVersion;

    /** 渠道 */
    protected String partner;

    /** 请求时间 */
    protected String requestTime;

    /** 签名 */
    protected String sign;
}
