package com.ln.xproject.application.service;

import java.util.List;

import com.ln.xproject.application.vo.ApplicationAuditLogListVo;
import com.ln.xproject.base.service.BaseService;
import com.ln.xproject.application.model.ApplicationAuditLog;

public interface ApplicationAuditLogService extends BaseService<ApplicationAuditLog> {

    List<ApplicationAuditLogListVo> getByApplicationNo(Long applicationNo);

    List<ApplicationAuditLog> findAll(Long applicationId);

}
