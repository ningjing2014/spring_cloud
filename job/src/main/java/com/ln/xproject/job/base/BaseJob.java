package com.ln.xproject.job.base;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.ln.xproject.base.spring.SpringContext;
import com.ln.xproject.job.util.JobUtils;
import com.ln.xproject.redis.service.RedisService;
import com.ln.xproject.util.UniqueKeyUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseJob implements Job {
    private String dataParam;
    
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        // redis
        RedisService redisService = SpringContext.getBean(RedisService.class);
        // lock
        String lock = JobUtils.getJobRunningKey(getClass());
        // get lock
        if (!redisService.acquireLock(lock, JobUtils.JOB_RUNNING_KEY_EXPIRED)) {
            log.info("Job：{}，上次运行未结束，此次退出", getClass().getSimpleName());
            return;
        }
        String jobUniqueKey = UniqueKeyUtils.uniqueKey();
        try {
            log.info("Job：{}, UniqueKey：{}， 开始运行", getClass().getSimpleName(), jobUniqueKey);

            BaseJob job = SpringContext.getBean(getClass());
            job.run();
        } finally {
            // release lock
            redisService.releaseLock(lock);
        }
        log.info("Job：{}, UniqueKey：{}， 运行结束", getClass().getSimpleName(), jobUniqueKey);
    }

    public abstract void run();


    public String getDataParam() {
        return dataParam;
    }

    public void setDataParam(String dataParam) {
        this.dataParam = dataParam;
    }
}
