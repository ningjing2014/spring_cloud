package com.ln.xproject.job.controller;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.base.spring.SpringContext;
import com.ln.xproject.base.vo.JsonResultVo;
import com.ln.xproject.job.base.BaseJob;
import com.ln.xproject.job.model.TaskTimer;
import com.ln.xproject.job.service.TaskTimerService;
import com.ln.xproject.util.StringUtils;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/job")
@Slf4j
public class JobController {

    private static final String EXECUTE_ONCE_GROUP = "ONCE";
    @Autowired
    private TaskTimerService taskTimerService;

    @RequestMapping("/run/{taskClass}")
    public JsonResultVo run(@PathVariable("taskClass") String taskClass,
            @RequestBody(required = false) String dataParam) {

        if (StringUtils.isBlank(taskClass)) {
            return JsonResultVo.error(JsonResultVo.ERROR, "taskClass is empty.");
        }
        if (StringUtils.isNotBlank(dataParam)) {
            try {
                JSONObject.parseObject(dataParam);
            } catch (Exception e) {
                return JsonResultVo.error(JsonResultVo.ERROR, "dataParam不是合法的json.");
            }
        }
        BaseJob job = null;

        try {
            job = (BaseJob) SpringContext.getBean(taskClass);
        } catch (Exception e) {
            log.error("{} is not exists.", taskClass, e);
            return JsonResultVo.error(JsonResultVo.ERROR, taskClass + " is not exists.");
        }

        if (job == null) {
            log.error("{} is not exists.", taskClass);
            return JsonResultVo.error(JsonResultVo.ERROR, taskClass + " is not exists.");
        }

        try {
            job.setDataParam(dataParam);
            Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

            JobKey jobKey = new JobKey(taskClass, EXECUTE_ONCE_GROUP);

            JobDetail jobDetail = scheduler.getJobDetail(jobKey);

            if (jobDetail == null) {
                jobDetail = JobBuilder.newJob(job.getClass()).withIdentity(jobKey).storeDurably(true).build();

                scheduler.addJob(jobDetail, false);
            }

            TriggerKey triggerKey = new TriggerKey(StringUtils.join(getClass(), "_", taskClass),
                    EXECUTE_ONCE_GROUP);

            Trigger trigger = scheduler.getTrigger(triggerKey);

            if (trigger == null) {
                trigger = TriggerBuilder.newTrigger().forJob(jobDetail).withIdentity(triggerKey)
                        .withSchedule(SimpleScheduleBuilder.repeatHourlyForTotalCount(1)).build();

                scheduler.scheduleJob(trigger);
            } else {
                log.info("{} already run.", taskClass);
            }

            return JsonResultVo.success();
        } catch (Exception e) {
            log.error("{} start error.", taskClass, e);
            return JsonResultVo.error(JsonResultVo.ERROR, taskClass + " start error.");
        }
    }

    @RequestMapping("/getParam/{taskClass}")
    public JsonResultVo getParam(@PathVariable("taskClass") String taskClass) {
        if (StringUtils.isBlank(taskClass)) {
            return JsonResultVo.error(JsonResultVo.ERROR, "taskClass is empty.");
        }

        TaskTimer taskTimer = null;
        try {
            taskTimer = taskTimerService.loadByTaskClass(taskClass);
        } catch (Exception e) {
            log.error("{} is not exists.", taskClass, e);
            return JsonResultVo.error(JsonResultVo.ERROR, taskClass + " is not exists.");
        }

        if (taskTimer == null) {
            log.error("{} is not exists.", taskClass);
            return JsonResultVo.error(JsonResultVo.ERROR, taskClass + " is not exists.");
        }

        if (StringUtils.isBlank(taskTimer.getDataParam())) {
            JsonResultVo resultVo = JsonResultVo.success();
            resultVo.addData("param", new JSONObject());
            return resultVo;
        } else {
            JsonResultVo resultVo = JsonResultVo.success();
            resultVo.addData("param", JSONObject.parseObject(taskTimer.getDataParam()));
            return resultVo;
        }
    }
}
