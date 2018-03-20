package com.ln.xproject.job.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.spring.SpringContext;
import com.ln.xproject.job.base.BaseJob;
import com.ln.xproject.job.constants.ParamType;
import com.ln.xproject.job.constants.TaskStatus;
import com.ln.xproject.job.constants.TaskType;
import com.ln.xproject.job.model.TaskTimer;
import com.ln.xproject.job.service.SysJobService;
import com.ln.xproject.job.service.TaskTimerParamParser;
import com.ln.xproject.job.service.TaskTimerParamService;
import com.ln.xproject.job.service.TaskTimerService;
import com.ln.xproject.job.util.JobUtils;
import com.ln.xproject.redis.service.RedisService;
import com.ln.xproject.util.CollectionUtils;
import com.ln.xproject.util.MapUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Lazy
@Transactional
public class SysJobServiceImpl implements SysJobService {

    private Scheduler scheduler;

    @Autowired
    private TaskTimerService taskTimerService;
    @Autowired
    private TaskTimerParamService taskTimerParamService;
    @Autowired
    private RedisService redisService;

    @PostConstruct
    public void init() {
        try {
            scheduler = StdSchedulerFactory.getDefaultScheduler();
            scheduler.start();
        } catch (Exception e) {
            log.error("初始化Scheduler出错", e);
        }
        // 释放所有未释放的锁
        releaseAllLock();
        // 启动所有Job
        runAllJob();
    }

    @Override
    public void runJobById(Long id) throws Exception {
        runJob(taskTimerService.load(id));
    }

    @Override
    public void runJobByTaskClass(String taskClass) throws Exception {
        runJob(taskTimerService.loadByTaskClass(taskClass));
    }

    @Override
    public void runAllJob() {
        List<TaskTimer> taskTimers = taskTimerService.findRunningTaskTimer();
        if (CollectionUtils.isEmpty(taskTimers)) {
            return;
        }
        for (TaskTimer taskTimer : taskTimers) {
            if (taskTimer.getTaskStatus() == TaskStatus.RUNNING) {
                try {
                    runJob(taskTimer);
                } catch (Exception e) {
                    log.error("启动任务{}出错", taskTimer.getTaskClass(), e);
                }
            }
        }
    }

    private void runJob(TaskTimer taskTimer) throws Exception {
        Assert.notNull(taskTimer, "任务定时器");

        log.info("启动任务：{}", taskTimer.getTaskName());

        Map<ParamType, String> paramMapping = taskTimerParamService.findParamMapping(taskTimer.getId());
        if (MapUtils.isEmpty(paramMapping)) {
            throw ServiceException.exception(CodeConstants.C_10101001, "任务定时器参数");
        }

        log.info("任务参数：{}", paramMapping);

        BaseJob job = (BaseJob) SpringContext.getBean(taskTimer.getTaskClass());

        JobDetail jobDetail = JobBuilder.newJob(job.getClass())
                .withIdentity(taskTimer.getTaskClass(), Scheduler.DEFAULT_GROUP).build();

        List<Trigger> triggers = getParser(taskTimer.getTaskType()).parse(taskTimer.getTaskClass(), paramMapping);
        if (CollectionUtils.isEmpty(triggers)) {
            throw ServiceException.exception(CodeConstants.C_10101001, "任务定时器触发器");
        }

        for (Trigger trigger : triggers) {
            scheduler.scheduleJob(jobDetail, trigger);
        }
    }

    private TaskTimerParamParser getParser(TaskType taskType) {
        TaskTimerParamParser parser = null;
        switch (taskType) {
            case TIMING_TASK:
                parser = new TimingTaskTimerParamParser();
                break;
            case LOOP_TASK:
                parser = new LoopTaskTimerParamParser();
                break;
            case CRON_TASK:
                parser = new CronTaskTimerParamParser();
                break;
            default:
                throw new RuntimeException("不可到达的分支");
        }
        return parser;
    }

    @Override
    public void stopJob(Long id) throws Exception {
        stopJob(taskTimerService.load(id));
    }

    @Override
    public void stopJob(String taskClass) throws Exception {
        stopJob(taskTimerService.loadByTaskClass(taskClass));
    }

    @Override
    public void stopAllJob() {
        List<TaskTimer> taskTimers = taskTimerService.findAll();
        if (CollectionUtils.isEmpty(taskTimers)) {
            return;
        }
        for (TaskTimer taskTimer : taskTimers) {
            try {
                stopJob(taskTimer);
            } catch (Exception e) {
                log.error("关闭任务{}出错", taskTimer.getTaskClass(), e);
            }
        }
    }

    private void stopJob(TaskTimer taskTimer) throws Exception {
        Assert.notNull(taskTimer, "任务定时器");

        JobKey jobKey = new JobKey(taskTimer.getTaskClass(), Scheduler.DEFAULT_GROUP);

        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return;
        }

        List<? extends Trigger> triggers = scheduler.getTriggersOfJob(jobKey);
        if (CollectionUtils.isNotEmpty(triggers)) {
            for (Trigger trigger : triggers) {
                scheduler.unscheduleJob(trigger.getKey());
            }
        }
        scheduler.deleteJob(jobKey);
    }

    /**
     * 释放所有未释放的锁
     */
    private void releaseAllLock() {
        Set<String> keys = redisService.keys(JobUtils.JOB_RUNNING_KEY_PREFIX + "*");
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        keys.forEach(key -> redisService.releaseLock(key));
    }

}
