package com.ln.xproject.job.service;

import java.util.List;

import com.ln.xproject.base.service.BaseService;
import com.ln.xproject.job.model.TaskTimer;
import com.ln.xproject.job.vo.TaskTimerListResponseVo;
import com.ln.xproject.job.vo.TaskTimerRequestVo;

public interface TaskTimerService extends BaseService<TaskTimer> {

    TaskTimer loadByTaskClass(String taskClass);

    List<TaskTimer> findRunningTaskTimer();

    List<TaskTimer> findStoppingTaskTimer();

    TaskTimerListResponseVo findTaskTimerList(TaskTimerRequestVo queryVo);

    void updateTaskTimerStatus(TaskTimerRequestVo requestVo);

}
