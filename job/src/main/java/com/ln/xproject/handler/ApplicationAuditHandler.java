package com.ln.xproject.handler;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.application.constants.ApplicationAuditStatus;
import com.ln.xproject.application.constants.ApplicationAuditType;
import com.ln.xproject.application.model.ApplicationAudit;
import com.ln.xproject.job.constants.Constants;
import com.ln.xproject.job.constants.DbTableStatus;
import com.ln.xproject.job.constants.JobMachineStatus;
import com.ln.xproject.job.model.Job;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationAuditHandler extends BaseHandler implements Runnable {

    public ApplicationAuditHandler(Job job) {
        super(job);
    }

    @Override
    public void run() {
        try {
            Long applicationId = JSONObject.parseObject(super.getJob().getJobParam())
                    .getLongValue(Constants.JOB_PARAM_APPLICATION_ID);
            ApplicationAudit audit = this.getApplicationAuditService().load(applicationId);
            ApplicationAuditStatus auditStauts = ApplicationAuditStatus.APPROVE;

            if (audit.getAuditResult() != ApplicationAuditStatus.APPROVE
                    && audit.getAuditResult() != ApplicationAuditStatus.REJECT
                    && audit.getAuditResult() != ApplicationAuditStatus.SPECIAL) {
                this.getApplicationService().updateAuditStatus(applicationId, auditStauts, null,
                        ApplicationAuditType.SYSTEM, "");
                if (auditStauts == ApplicationAuditStatus.APPROVE) {
                    saveNextJob(JobMachineStatus.APPL_TO_PUSH, super.getJob().getJobParam());
                } else if (auditStauts == ApplicationAuditStatus.REJECT) {
                    saveNextJob(JobMachineStatus.APPL_TO_RECALL, super.getJob().getJobParam());
                }
            }

            this.saveJob(DbTableStatus.SUCCESS, null);

        } catch (Exception e) {
            log.error("执行job出错. jobId:" + super.getJob().getId(), e);
            this.saveJob(DbTableStatus.NOTICE_MANUAL, e.getMessage());

        }

    }

}
