package com.ln.xproject.task;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ln.xproject.application.model.Application;
import com.ln.xproject.application.service.ApplicationService;
import com.ln.xproject.job.base.BaseJob;
import com.ln.xproject.job.service.ApplicationJobService;

import lombok.extern.slf4j.Slf4j;

/**
 * 初始化进件，获取征信信息
 * 
 * @author taixin
 */

@Slf4j
@Service
public class ApplicationInitJob extends BaseJob {

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationJobService applicationJobService;

    @Override
    public void run() {
        log.info("初始化进件任务开始");
        try {
            List<Application> applList = applicationService.listApplToInit();

            if (CollectionUtils.isEmpty(applList)) {
                log.info("需要初始化的进件为空，任务结束");
                return;
            }
            applList.forEach(appl -> {
                applicationJobService.initAppl(appl.getId());
            });
        } catch (Exception e) {
            log.info("初始化进件任务异常", e);
        }
        log.info("初始化进件任务结束");
    }

}
