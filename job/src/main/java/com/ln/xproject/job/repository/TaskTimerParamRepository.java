package com.ln.xproject.job.repository;

import java.util.List;

import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.job.model.TaskTimerParam;

public interface TaskTimerParamRepository extends BaseRepository<TaskTimerParam> {

    List<TaskTimerParam> findByTaskTimerId(Long taskTimerId);

}
