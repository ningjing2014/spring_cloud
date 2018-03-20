package com.ln.xproject.job.service;

import java.util.List;
import java.util.Map;

import com.ln.xproject.base.service.BaseService;
import com.ln.xproject.job.constants.ParamType;
import com.ln.xproject.job.model.TaskTimerParam;
import com.ln.xproject.job.vo.TaskTimerParamRequestVo;
import com.ln.xproject.job.vo.TaskTimerParamVo;

public interface TaskTimerParamService extends BaseService<TaskTimerParam> {

    Map<ParamType, String> findParamMapping(Long taskTimerId);

    List<TaskTimerParamVo> findTaskTimerParamByTaskTimerId(TaskTimerParamRequestVo messageVo);

    void updateTaskTimerParam(TaskTimerParamRequestVo requestVo);

}
