package com.ln.xproject.job.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.base.config.CostomPageRequest;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.job.constants.DbTableStatus;
import com.ln.xproject.job.constants.JobMachineStatus;
import com.ln.xproject.job.constants.JobType;
import com.ln.xproject.job.model.Job;
import com.ln.xproject.job.repository.JobRepository;
import com.ln.xproject.job.service.JobService;
import com.ln.xproject.util.DateUtils;
import com.ln.xproject.util.StringUtils;

@Service
@Transactional
public class JobServiceImpl extends BaseServiceImpl<Job, JobRepository> implements JobService {

    @Autowired
    @Override
    protected void setRepository(JobRepository repository) {
        super.repository = repository;
    }

    public static final String JOB_PARAM_NEXT_START_TIME = "jobNextStartTime";

    @Override
    public Page<Job> getToDealJobList(int pageNum, int pageSize, Long lastId) {
        CostomPageRequest pageable = new CostomPageRequest(pageNum, pageSize, Sort.Direction.ASC, "id");
        return this.repository.findByJobStatusAndIdGreaterThan(new Integer(DbTableStatus.INIT.getStatus()).byteValue(),
                lastId, pageable);

    }

    @Override
    public void saveOrUpdate(Job job) {
        trimLastError(job);
        if (job.getId() == null) {
            super.save(job);
        } else {
            if (job.getExecuteTimes() == null) {
                job.setExecuteTimes(1);
            } else {
                job.setExecuteTimes(job.getExecuteTimes() + 1);
            }
            if (DbTableStatus.INIT == DbTableStatus.getEnum(job.getJobStatus())) {
                if (JobType.TIME_JOB != JobType.getEnum(job.getJobType())) {
                    job.setJobStartTime(DateUtils.getDelayDate((int) (job.getExecuteTimes() * 30L)));
                } else {
                    String jobParam = job.getJobParam();
                    if (StringUtils.isBlank(jobParam)) {
                        jobParam = new JSONObject().toJSONString();
                    }
                    JSONObject jobParamJson = JSONObject.parseObject(jobParam);
                    jobParamJson.put(JOB_PARAM_NEXT_START_TIME,
                            DateUtils.getDelayDate((int) (job.getExecuteTimes() * 30L)).getTime());
                    job.setJobParam(jobParamJson.toJSONString());
                }
            }
            super.update(job);
        }
    }

    private void trimLastError(Job job) {
        if (StringUtils.isNotBlank(job.getLastError())) {
            if (job.getLastError().length() > 255) {
                job.setLastError(job.getLastError().substring(0, 255));
            }
        }
    }

    public boolean isJobDone(Job job) {
        return (DbTableStatus.FAIL == DbTableStatus.getEnum(job.getJobStatus()))
                || (DbTableStatus.SUCCESS == DbTableStatus.getEnum(job.getJobStatus()))
                || (DbTableStatus.NOTICE_MANUAL == DbTableStatus.getEnum(job.getJobStatus()));
    }

    /**
     * job指定了下次开始时间
     *
     * @param job
     */
    @Override
    public void saveOrUpdateWithJobStartTime(Job job) {
        trimLastError(job);
        if (job.getId() == null) {
            save(job);
        } else {
            if (job.getExecuteTimes() == null) {
                job.setExecuteTimes(1);
            } else {
                job.setExecuteTimes(job.getExecuteTimes() + 1);
            }
            update(job);
        }
    }

    @Override
    public void saveNotUpdateExecuteTimes(Job job) {
        trimLastError(job);
        update(job);
    }

    @Override
    public void updateJobState(String ids, Integer jobStatus, String lastError) throws Exception {
        // 校验参数
        Assert.notBlank(ids, "job表主键");
        DbTableStatus dbTableStatus = DbTableStatus.getEnum(jobStatus);
        if (dbTableStatus == null) {
            throw new Exception("更改Job的jobStatus为" + jobStatus + "的状态不存在状态枚举");
        }
        List<String> idList = StringUtils.splitToList(ids, ",");
        if (!CollectionUtils.isEmpty(idList)) {
            for (String id : idList) {
                Job job = load(Long.parseLong(id));
                if (job == null) {
                    throw new Exception("Job的id为" + id + "的Job不存在");
                }
                job.setJobStatus((byte) jobStatus.intValue());
                job.setLastError(lastError);
                update(job);
            }
        }
    }
    @Override
    public void generateJob(JobMachineStatus jobMachineStatus, Long primaryKey, JSONObject jsonParam,
            Date jobStartTime) {
        Job job = new Job();
        job.setMachineStatus(jobMachineStatus.getStatus());
        job.setPrimaryKey(primaryKey);
        job.setJobType(new Integer(JobType.COMMON_JOB.getType()).byteValue());
        job.setJobStatus(new Integer(DbTableStatus.INIT.getStatus()).byteValue());
        job.setJobStartTime(jobStartTime);
        job.setJobParam(jsonParam.toString());
        this.saveOrUpdate(job);
    }
    @Override
    public void generateJob(JobMachineStatus jobMachineStatus, Long primaryKey, JSONObject jsonParam) {
        Job job = new Job();
        job.setMachineStatus(jobMachineStatus.getStatus());
        job.setPrimaryKey(primaryKey);
        job.setJobType(new Integer(JobType.COMMON_JOB.getType()).byteValue());
        job.setJobStatus(new Integer(DbTableStatus.INIT.getStatus()).byteValue());
        job.setJobParam(jsonParam.toString());
        this.saveOrUpdate(job);
    }
}
