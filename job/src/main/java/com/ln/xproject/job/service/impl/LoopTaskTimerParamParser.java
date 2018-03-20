package com.ln.xproject.job.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.quartz.Scheduler;
import org.quartz.SimpleScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.job.constants.ParamType;
import com.ln.xproject.job.service.TaskTimerParamParser;
import com.ln.xproject.util.DateUtils;

public class LoopTaskTimerParamParser implements TaskTimerParamParser {

    @Override
    public List<Trigger> parse(String name, Map<ParamType, String> params) {
        checkParam(name, params);

        // 延迟时间
        Long delay = Long.parseLong(params.get(ParamType.LOOP_DELAY_PARAM));
        // 间隔时间
        Long interval = Long.parseLong(params.get(ParamType.LOOP_INTERVAL_PARAM));

        // 开始时间（当前时间 + 延迟时间）
        Date start = DateUtils.addMilliseconds(new Date(), delay.intValue());

        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(name, Scheduler.DEFAULT_GROUP).startAt(start)
                .withSchedule(
                        SimpleScheduleBuilder.simpleSchedule().withIntervalInMilliseconds(interval).repeatForever())
                .build();

        List<Trigger> triggers = new ArrayList<>(1);
        triggers.add(trigger);

        return triggers;
    }

    private void checkParam(String name, Map<ParamType, String> params) {
        Assert.notBlank(name, "名字");
        Assert.notNull(params, "参数列表");
        if (!params.containsKey(ParamType.LOOP_DELAY_PARAM)) {
            throw ServiceException.exception(CodeConstants.C_10101001, "循环任务延迟参数");
        }
        try {
            Long.parseLong(params.get(ParamType.LOOP_DELAY_PARAM));
        } catch (Exception e) {
            throw ServiceException.exception(CodeConstants.C_10101006, "循环任务延迟参数");
        }
        if (!params.containsKey(ParamType.LOOP_INTERVAL_PARAM)) {
            throw ServiceException.exception(CodeConstants.C_10101001, "循环任务间隔参数");
        }
        try {
            Long.parseLong(params.get(ParamType.LOOP_INTERVAL_PARAM));
        } catch (Exception e) {
            throw ServiceException.exception(CodeConstants.C_10101006, "循环任务间隔参数");
        }
    }

}
