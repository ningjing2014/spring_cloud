package com.ln.xproject.job.service;

import java.util.List;
import java.util.Map;

import org.quartz.Trigger;

import com.ln.xproject.job.constants.ParamType;

public interface TaskTimerParamParser {

    /**
     * 解析
     * 
     * @param name
     * @param params
     * @return
     */
    List<Trigger> parse(String name, Map<ParamType, String> params);

}
