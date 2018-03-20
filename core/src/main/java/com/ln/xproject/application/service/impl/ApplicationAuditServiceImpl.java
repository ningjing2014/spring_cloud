package com.ln.xproject.application.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.application.constants.ApplicationAuditStatus;
import com.ln.xproject.application.constants.ApplicationAuditType;
import com.ln.xproject.application.model.Application;
import com.ln.xproject.application.model.ApplicationAudit;
import com.ln.xproject.application.model.ApplicationAuditLog;
import com.ln.xproject.application.repository.ApplicationAuditRepository;
import com.ln.xproject.application.service.ApplicationAuditLogService;
import com.ln.xproject.application.service.ApplicationAuditService;
import com.ln.xproject.application.service.ApplicationService;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.job.constants.Constants;
import com.ln.xproject.job.constants.JobMachineStatus;
import com.ln.xproject.job.service.JobService;
import com.ln.xproject.redis.service.RedisService;
import com.ln.xproject.system.model.SysUser;
import com.ln.xproject.system.service.SysUserRoleService;
import com.ln.xproject.system.service.SysUserService;
import com.ln.xproject.util.DateUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ApplicationAuditServiceImpl extends BaseServiceImpl<ApplicationAudit, ApplicationAuditRepository>
        implements ApplicationAuditService {

    @Autowired
    @Override
    protected void setRepository(ApplicationAuditRepository repository) {
        super.repository = repository;
    }

    @Autowired
    private RedisService redisService;
    @Autowired
    private SysUserRoleService sysUserRoleService;
    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private JobService jobService;
    @Autowired
    private ApplicationAuditLogService applicationAuditLogService;
    @Autowired
    private SysUserService sysUserService;

    private static final String ASSIGN_LOCK_KEY = "application_lock_key_%s";
    private static final Long AUTO_ASSIGN_ROLE_ID = 3l;
    private static final String AUTO_ASSIGN_LEFT_USER = "auto_assign_left_user";

    @Override
    public void addAudit(Long applId) {
        Assert.notNull(applId, "进件Id");

        ApplicationAudit audit = new ApplicationAudit();
        audit.setApplicationId(applId);
        audit.setAuditResult(ApplicationAuditStatus.TO_DISTRIBUTE);
        super.save(audit);
    }

    @Override
    public ApplicationAudit loadByApplicationId(Long applicationId) {
        ApplicationAudit audit = this.repository.findByApplicationId(applicationId);
        Assert.notNull(audit, "审核信息");
        return audit;
    }

    @Override
    public List<ApplicationAudit> loadByOperatorUserId(Long userId) {
        List<ApplicationAudit> audit = this.repository.findByOperatorUserId(userId);
        return audit;
    }

    @Override
    public ApplicationAudit loadByApplicationIdWithoutException(Long applicationId) {
        ApplicationAudit audit = this.repository.findByApplicationId(applicationId);
        return audit;
    }

    @Override
    public void assign(Long applicationId, Long userId) {
        Assert.notNull(applicationId, "进件主键");
        String lockKey = String.format(ASSIGN_LOCK_KEY, applicationId);
        ApplicationAudit audit = this.loadByApplicationId(applicationId);

        if (audit.getOperatorUserId() != null) {
            log.info("进件application:{}已分配审核人员{}", applicationId, audit.getOperatorUserId());
            throw ExceptionUtils.commonError("进件已分配审核人员");
        }
        boolean acquireLock = redisService.acquireLock(lockKey, 60);
        if (!acquireLock) {
            throw ExceptionUtils.commonError("获取分配所失败");
        }
        try {
            if (userId == null) {
                userId = getAssginTaskUserId();
            }
            if (userId == null) {
                log.warn("assign application{} not found verify user!!!", applicationId);
                return;
            }
            Application application = this.applicationService.load(applicationId);

            // 分配审核员
            log.info("进件[{}]审核人员变更为[{}]", applicationId, userId);
            audit.setAuditResult(ApplicationAuditStatus.TO_AUDIT);
            ApplicationAuditStatus.checkAuditStatus(application.getAuditStatus(), ApplicationAuditStatus.TO_AUDIT);

            audit.setOperatorUserId(userId);
            this.update(audit);

            // 更新审核状态
            application.setAuditStatus(ApplicationAuditStatus.TO_AUDIT);
            this.applicationService.update(application);

            generateAudoVerifyJob(applicationId);
        } finally {
            redisService.releaseLock(lockKey);
        }
    }

    @Override
    public void generateAudoVerifyJob(Long applicationId) {
        Random random = new Random();
        // 2分45秒+30秒内的随机数
        int startTimes = 2 * 60 + 45 + random.nextInt(30);
        JSONObject jsonParam = new JSONObject();
        jsonParam.put(Constants.JOB_PARAM_APPLICATION_ID, applicationId);
        jobService.generateJob(JobMachineStatus.APPL_TO_AUDIT, applicationId, jsonParam,
                DateUtils.getDelayDate(startTimes));
    }

    @Override
    public void updateAuditStatus(Long applicationId, ApplicationAuditStatus applicationAuditStatus,
            Long operatorUserId, ApplicationAuditType applicationAuditType, String remark) {
        ApplicationAudit audit = this.loadByApplicationId(applicationId);
        ApplicationAuditStatus fromAuditStatus = audit.getAuditResult();
        audit.setAuditResult(applicationAuditStatus);
        audit.setAuditTime(new Date());
        audit.setAuditType(applicationAuditType);
        if (null != operatorUserId) {
            audit.setOperatorUserId(operatorUserId);
        }
        audit.setRejectReason(remark);
        this.update(audit);

        ApplicationAuditLog log = new ApplicationAuditLog();
        log.setAuditId(audit.getId());
        log.setFromAuditResult(fromAuditStatus);
        log.setToAuditResult(applicationAuditStatus);
        log.setAuditTime(new Date());
        log.setAuditType(applicationAuditType);
        log.setOperatorUserId(audit.getOperatorUserId());
        log.setApplicationId(applicationId);
        log.setRemark(remark);
        this.applicationAuditLogService.save(log);
    }

    public Long getAssginTaskUserId() {

        // 查看列表中是否还有未分配的用户，如果没有，则把全局用户复制到未分配的列表中
        if (this.redisService.opsForSet().size(AUTO_ASSIGN_LEFT_USER) == 0) {
            Set<String> users = this.sysUserRoleService.getUserByRoleId(AUTO_ASSIGN_ROLE_ID);
            this.redisService.opsForSet().add(AUTO_ASSIGN_LEFT_USER, users.toArray(new String[] {}));
        }
        // 获取一个用户key
        String userId = this.redisService.opsForSet().pop(AUTO_ASSIGN_LEFT_USER);
        return userId == null ? null : Long.valueOf(userId);
    }

    @Override
    public Boolean isDistribute(String applicationId, Long sysUserId) {
        try {
            Long applNo = Long.parseLong(applicationId);

            Application application = applicationService.get(applNo);
            Assert.notExist(application, "进件信息不存在");

            SysUser sysUser = sysUserService.get(sysUserId);
            Assert.notExist(sysUser, "分配人员不存在");

            this.assign(applNo, sysUserId);
            return Boolean.TRUE;
        } catch (ServiceException e) {
            log.error("进件号 {} 分配失败原因 {}", applicationId, e.getMessage(), e);
            return Boolean.FALSE;
        } catch (Exception e) {
            log.error("进件号 {} 分配失败", applicationId, e);
            return Boolean.FALSE;
        }
    }

}
