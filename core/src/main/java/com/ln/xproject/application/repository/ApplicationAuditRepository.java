package com.ln.xproject.application.repository;

import java.util.List;

import com.ln.xproject.application.model.ApplicationAudit;
import com.ln.xproject.base.repository.BaseRepository;

public interface ApplicationAuditRepository extends BaseRepository<ApplicationAudit> {

    ApplicationAudit findByApplicationId(Long applicationId);
    List<ApplicationAudit> findByOperatorUserId(Long userId);

}
