package com.ln.xproject.job.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.job.constants.ParamType;
import com.ln.xproject.job.model.TaskTimerParam;
import com.ln.xproject.job.repository.TaskTimerParamRepository;
import com.ln.xproject.job.service.TaskTimerParamService;
import com.ln.xproject.job.vo.TaskTimerParamRequestVo;
import com.ln.xproject.job.vo.TaskTimerParamVo;
import com.ln.xproject.util.CollectionUtils;

@Service
@Transactional
public class TaskTimerParamServiceImpl extends BaseServiceImpl<TaskTimerParam, TaskTimerParamRepository>
        implements TaskTimerParamService {

    @Autowired
    @Override
    protected void setRepository(TaskTimerParamRepository repository) {
        super.repository = repository;
    }

    @Override
    public Map<ParamType, String> findParamMapping(Long taskTimerId) {
        Assert.notNull(taskTimerId, "任务定时器");

        List<TaskTimerParam> params = repository.findByTaskTimerId(taskTimerId);
        if (CollectionUtils.isEmpty(params)) {
            return null;
        }

        Map<ParamType, String> mapping = new HashMap<>();

        for (TaskTimerParam param : params) {
            mapping.put(param.getParamType(), param.getParamValue());
        }

        return mapping;
    }

    @Override
    public List<TaskTimerParamVo> findTaskTimerParamByTaskTimerId(TaskTimerParamRequestVo messageVo) {
        Long taskTimerId = messageVo.getTaskTimerId();
        Assert.notNull(taskTimerId, "taskTimerId");
        List<TaskTimerParam> params = repository.findByTaskTimerId(taskTimerId);
        List<TaskTimerParamVo> list = new ArrayList<TaskTimerParamVo>();
        for (TaskTimerParam param : params) {
            list.add(buildTaskTimerParamVo(param));
        }
        return list;
    }

    @Override
    public void updateTaskTimerParam(TaskTimerParamRequestVo requestVo) {
        Assert.notNull(requestVo.getId(), "taskTimerParam主键");
        Assert.notNull(requestVo.getTaskTimerId(), "taskTimerId");
        Assert.notNull(requestVo.getParamValue(), "paramValue");
        TaskTimerParam taskTimerParam = this.load(requestVo.getId());
        if (!taskTimerParam.getTaskTimerId().equals(requestVo.getTaskTimerId())) {
            throw ServiceException.exception(CodeConstants.C_10101005, "taskTimer");
        }
        taskTimerParam.setParamValue(requestVo.getParamValue());
        this.update(taskTimerParam);
    }

    public TaskTimerParamVo buildTaskTimerParamVo(TaskTimerParam taskTimerParam) {
        if (taskTimerParam == null) {
            return null;
        }
        TaskTimerParamVo taskTimerParamVo = new TaskTimerParamVo();
        taskTimerParamVo.setId(taskTimerParam.getId());
        taskTimerParamVo.setTaskTimerId(taskTimerParam.getTaskTimerId());
        taskTimerParamVo.setParamName(taskTimerParam.getParamName());
        taskTimerParamVo.setParamType(taskTimerParam.getParamType().toString());
        taskTimerParamVo.setParamValue(taskTimerParam.getParamValue());
        taskTimerParamVo.setCreateTime(taskTimerParam.getCreateTime());
        taskTimerParamVo.setUpdateTime(taskTimerParam.getUpdateTime());
        return taskTimerParamVo;
    }

}
