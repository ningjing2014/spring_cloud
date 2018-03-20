package com.ln.xproject.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ln.xproject.application.model.Application;
import com.ln.xproject.application.service.ApplicationAuditService;
import com.ln.xproject.application.service.ApplicationService;
import com.ln.xproject.job.base.BaseJob;
import com.ln.xproject.util.CollectionUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 进件自动化分配
 */
@Slf4j
@Service
public class ApplicationAssignJob extends BaseJob {
    @Autowired
    private ApplicationAuditService applicationAuditService;
    @Autowired
    private ApplicationService applicationService;

    @Override
    public void run() {
        log.info("ApplicationInitJob start");
        List<Application> list = applicationService.getUnAssignApplication();

        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(rcd -> {
            try {
                this.applicationAuditService.assign(rcd.getId(), null);
            } catch (Exception e) {
                log.error("初始化进件异常 applicationId:{}", rcd.getId(), e);
            }
        });
        log.info("ApplicationInitJob end");
    }

}
