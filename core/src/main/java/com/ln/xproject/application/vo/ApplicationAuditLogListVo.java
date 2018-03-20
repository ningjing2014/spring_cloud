package com.ln.xproject.application.vo;

import java.io.Serializable;

import lombok.Data;

@Data
public class ApplicationAuditLogListVo implements Serializable {

    private static final long serialVersionUID = -45400597512667253L;

    /** 操作人姓名 */
    private String auditorName;

    /** 操作前状态 */
    private String fromAuditStatus;

    /** 审核时间 */
    private String operateTime;

    /** 操作后状态 */
    private String toAuditStatus;

}
