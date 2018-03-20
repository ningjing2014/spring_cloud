package com.ln.xproject.application.repository;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.ln.xproject.application.constants.ApplicationAuditStatus;
import com.ln.xproject.application.constants.ApplicationChannel;
import com.ln.xproject.application.model.Application;
import com.ln.xproject.base.repository.BaseRepository;

public interface ApplicationRepository extends BaseRepository<Application> {

    List<Application> findByChannelLoanIdAndApplicationChannel(String channelLoanId, ApplicationChannel channel,
            Sort sort);

    List<Application> findByIsInitedAndAuditStatus(Boolean isInited, ApplicationAuditStatus auditStatus);

    Application findByVerifySerialNo(String verifySerialNo);

    List<Application> findByIsInited(Boolean isInited);

}
