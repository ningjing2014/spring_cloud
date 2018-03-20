package com.ln.xproject.application.repository;

import java.util.List;

import com.ln.xproject.application.constants.ApplicationAuditStatus;
import com.ln.xproject.application.constants.ApplicationChannel;
import com.ln.xproject.application.model.Application;
import com.ln.xproject.application.model.CreditReport;
import com.ln.xproject.base.repository.BaseRepository;

public interface CreditReportRepository extends BaseRepository<CreditReport> {

    List<CreditReport> findByApplicationId(Long applicationId);

}
