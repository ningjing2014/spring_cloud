package com.ln.xproject.task;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.quartz.DisallowConcurrentExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.ln.xproject.job.base.BaseJob;
import com.ln.xproject.job.constants.DbTableStatus;
import com.ln.xproject.job.constants.JobMachineStatus;
import com.ln.xproject.handler.ApplicationAuditHandler;
import com.ln.xproject.handler.ApplicationQueryHandler;
import com.ln.xproject.handler.ApplicationRecallHandler;
import com.ln.xproject.handler.ApplicationToPushHandler;
import com.ln.xproject.job.model.Job;
import com.ln.xproject.job.service.JobService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Component
@DisallowConcurrentExecution
@Data
@Slf4j
public class DistributeJobController extends BaseJob {
    @Autowired
    private JobService jobService;
    public static ThreadPoolExecutor poolExecutor;
    public static ExecutorService singleThreadExecutor;

    @PostConstruct
    public void init() {
        poolExecutor = new ThreadPoolExecutor(10, 20, 600, TimeUnit.SECONDS,
                new ArrayBlockingQueue<Runnable>(10000, true), new ThreadPoolExecutor.CallerRunsPolicy());
        singleThreadExecutor = Executors.newSingleThreadExecutor();
    }

    @Override
    public void run() {
        try {
            log.info("开始分配任务.");
            distributeJob();
            log.info("分配任务结束.");
        } catch (Exception e) {
            log.error("分配任务出错.", e);
        }
    }

    private Job dealingJob(Job job) {
        job.setJobStatus((byte) DbTableStatus.DEALING.getStatus());
        jobService.saveNotUpdateExecuteTimes(job);
        return jobService.load(job.getId());
    }

    public void distributeJob() {
        if (poolExecutor.isShutdown()) {
            return;
        }
        int pageNo = 1;
        int pageSize = 50;
        Long lastId = 0L;
        while (true) {
            try {
                Page<Job> pageInfo = jobService.getToDealJobList(pageNo, pageSize, lastId);
                List<Job> jobList = pageInfo.getContent();
                if (pageInfo.getTotalElements() > 0L && !CollectionUtils.isEmpty(jobList)) {
                    for (Job job : jobList) {
                        lastId = job.getId();
                        Date now = new Date();
                        Date startDate = job.getJobStartTime();
                        if (startDate != null && now.before(startDate)) {
                            continue;
                        }
                        JobMachineStatus jobMachineStatus = JobMachineStatus.getEnum(job.getMachineStatus());
                        if (jobMachineStatus == null) {
                            log.error("job枚举没找到:" + job);
                            continue;
                        }
                        distributeJobToDeal(jobMachineStatus, job);
                    }
                } else {
                    break;
                }
            } catch (Exception e) {
                log.error("获取任务列表失败.", e);
                break;
            }
        }
    }

    private void distributeJobToDeal(JobMachineStatus jobMachineStatus, Job job) {
        switch (jobMachineStatus) {
            case APPL_TO_AUDIT:
                poolExecutor.execute(new ApplicationAuditHandler(dealingJob(job)));
                break;
            case APPL_TO_PUSH:
                poolExecutor.execute(new ApplicationToPushHandler(dealingJob(job)));
                break;
            case APPL_TO_QUERY:
                poolExecutor.execute(new ApplicationQueryHandler(dealingJob(job)));
                break;
            case APPL_TO_RECALL:
                poolExecutor.execute(new ApplicationRecallHandler(dealingJob(job)));
                break;

            default:
                break;
        }
    }
}
