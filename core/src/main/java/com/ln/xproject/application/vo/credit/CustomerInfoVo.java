package com.ln.xproject.application.vo.credit;

import java.io.Serializable;

import lombok.Data;
/*
* 央行征信 -- 用户基本信息
* */

@Data
public class CustomerInfoVo implements Serializable {

    private static final long serialVersionUID = -45400597512667253L;

    /** 报告中客户姓名 */
    private String custName;

    /** 报告中客户性别 */
    private String gender;

    /** 报告中客户证件号码 */
    private String idNo;

    /** 报告中客户证件类型 */
    private String idType;

    /** 报告中的报告时间 */
    private String reportTime;

    /** 报告中的查询时间 */
    private String searchTime;

}
