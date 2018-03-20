package com.ln.xproject.handler;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.client.RestTemplate;

import com.ln.xproject.api.service.ApplicationApiService;
import com.ln.xproject.application.service.ApplicationAuditService;
import com.ln.xproject.application.service.ApplicationCardInfoService;
import com.ln.xproject.application.service.ApplicationService;
import com.ln.xproject.application.service.ApplicationUserService;
import com.ln.xproject.base.server.PayServer;
import com.ln.xproject.base.server.UcreditServer;
import com.ln.xproject.base.server.VerifyServer;
import com.ln.xproject.base.spring.SpringContext;
import com.ln.xproject.job.constants.DbTableStatus;
import com.ln.xproject.job.constants.JobMachineStatus;
import com.ln.xproject.job.constants.JobType;
import com.ln.xproject.job.model.Job;
import com.ln.xproject.job.service.JobService;
import com.ln.xproject.util.StringUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class BaseHandler {

    private static Map<String, ChannelInterface> handlers = new ConcurrentHashMap<String, ChannelInterface>();

    private JobService jobService;
    private ApplicationAuditService applicationAuditService;
    private ApplicationService applicationService;
    private RestTemplate restTemplate;
    private ApplicationUserService applicationUserService;
    private ApplicationCardInfoService applicationCardInfoService;
    private ApplicationApiService applicationApiService;
    private PayServer payServer;
    private VerifyServer verifyServer;
    private UcreditServer ucreditServer;

    private Job job;

    static {
        Map<String, ChannelInterface> interImpls = SpringContext.getApplicationContext()
                .getBeansOfType(ChannelInterface.class);
        Iterator<ChannelInterface> iterator = interImpls.values().iterator();
        ChannelInterface handler = null;
        while (iterator.hasNext()) {
            handler = iterator.next();
            handlers.put(handler.getChannel() + "##" + handler.getJobMachineStatus().getStatus(), handler);
        }
        log.info("BaseHandler init()");
    }

    public BaseHandler(Job job) {
        jobService = (JobService) SpringContext.getBean("jobServiceImpl");
        applicationAuditService = (ApplicationAuditService) SpringContext.getBean("applicationAuditServiceImpl");
        applicationService = (ApplicationService) SpringContext.getBean("applicationServiceImpl");
        restTemplate = (RestTemplate) SpringContext.getBean("restTemplate");
        applicationUserService = (ApplicationUserService) SpringContext.getBean("applicationUserServiceImpl");
        applicationCardInfoService = (ApplicationCardInfoService) SpringContext
                .getBean("applicationCardInfoServiceImpl");
        applicationApiService = (ApplicationApiService) SpringContext.getBean("applicationApiServiceImpl");
        this.job = job;
        this.payServer = (PayServer) SpringContext.getBean("payServer");
        this.verifyServer = (VerifyServer) SpringContext.getBean("verifyServer");
        this.ucreditServer = (UcreditServer) SpringContext.getBean("ucreditServer");

    }

    public void saveJob(DbTableStatus status, String lastError) {
        job.setJobStatus(new Integer(status.getStatus()).byteValue());
        if (lastError == null) {
            lastError = "";
        }
        job.setLastError(lastError);
        jobService.saveOrUpdate(job);
    }

    public void saveWithJobStartTime(DbTableStatus status, String lastError) {
        job.setJobStatus(new Integer(status.getStatus()).byteValue());
        if (lastError == null) {
            lastError = "";
        }
        job.setLastError(lastError);
        jobService.saveOrUpdateWithJobStartTime(job);
    }

    public void saveNotUpdateExecuteTimes(DbTableStatus status, String lastError) {
        job.setJobStatus(new Integer(status.getStatus()).byteValue());
        job.setLastError(lastError);
        jobService.saveNotUpdateExecuteTimes(job);
    }

    public void saveNextJob(JobMachineStatus nextStatus, String jobParam) {
        Job nextJob = new Job();
        nextJob.setMachineStatus(nextStatus.getStatus());
        nextJob.setPrimaryKey(job.getPrimaryKey());
        nextJob.setJobType(new Integer(JobType.COMMON_JOB.getType()).byteValue());
        nextJob.setJobStatus(new Integer(DbTableStatus.INIT.getStatus()).byteValue());
        nextJob.setLastJobId(job.getId());
        nextJob.setJobParam(jobParam);
        jobService.saveOrUpdate(nextJob);
    }

    public void saveRelatedJob(JobMachineStatus nextStatus, String jobParam) {
        Job nextJob = new Job();
        nextJob.setMachineStatus(nextStatus.getStatus());
        nextJob.setPrimaryKey(job.getPrimaryKey());
        nextJob.setJobType(new Integer(JobType.COMMON_JOB.getType()).byteValue());
        nextJob.setJobStatus(new Integer(DbTableStatus.INIT.getStatus()).byteValue());
        nextJob.setLastJobId(this.getJob().getLastJobId());
        nextJob.setJobParam(jobParam);
        jobService.saveOrUpdate(nextJob);
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    private ChannelInterface getHandler(String partner, String jobMachineStatus) {
        return handlers.get(partner + "##" + jobMachineStatus);
    }

    public void runByHandler(String partner, Map<String, Object> partnerParamMap, BaseHandler handler) {
        getHandler(partner, getJob().getMachineStatus()).run(handler, partnerParamMap);
    }

    /**
     * 判断是否是测试平台的测试单号
     * 
     * @param caseOrderNo
     * @return
     */
    public boolean isTestUserCase(String caseOrderNo) {
        if (StringUtils.isNotBlank(caseOrderNo) && caseOrderNo.startsWith("TEST_")) {
            return true;
        }
        return false;
    }

}
