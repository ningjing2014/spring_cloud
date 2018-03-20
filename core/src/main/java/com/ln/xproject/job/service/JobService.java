package com.ln.xproject.job.service;

import java.util.Date;

import org.springframework.data.domain.Page;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.base.service.BaseService;
import com.ln.xproject.job.constants.JobMachineStatus;
import com.ln.xproject.job.model.Job;

public interface JobService extends BaseService<Job> {

    Page<Job> getToDealJobList(int pageNum, int pageSize, Long lastId);

    void saveOrUpdate(Job job);

    void saveOrUpdateWithJobStartTime(Job job);

    void saveNotUpdateExecuteTimes(Job job);

    void updateJobState(String ids, Integer jobStatus, String lastError) throws Exception;

    void generateJob(JobMachineStatus jobMachineStatus, Long primaryKey, JSONObject jsonParam, Date jobStartTime);

    void generateJob(JobMachineStatus jobMachineStatus, Long primaryKey, JSONObject jsonParam);
}
