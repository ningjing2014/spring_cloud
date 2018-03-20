package com.ln.xproject.api.vo;

import java.io.Serializable;
import java.math.BigDecimal;

import lombok.Data;

/**
 * 央行征信详情查询请求Vo
 * 
 * @author liyong
 */

@Data
public class CreditDetailRequestVo implements Serializable {

    private static final long serialVersionUID = -3597901087157778898L;

    private String applicationNo;

}
