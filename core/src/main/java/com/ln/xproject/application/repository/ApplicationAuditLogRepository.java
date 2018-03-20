package com.ln.xproject.application.repository;

import java.util.List;

import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.application.model.ApplicationAuditLog;

public interface ApplicationAuditLogRepository extends BaseRepository<ApplicationAuditLog> {

    List<ApplicationAuditLog> findByApplicationId(Long applicationId);
}
