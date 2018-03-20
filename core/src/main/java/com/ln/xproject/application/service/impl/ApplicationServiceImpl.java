package com.ln.xproject.application.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.application.constants.ApplicationAuditStatus;
import com.ln.xproject.application.constants.ApplicationAuditType;
import com.ln.xproject.application.constants.ApplicationChannel;
import com.ln.xproject.application.constants.ApplicationPushStatus;
import com.ln.xproject.application.constants.NoticeStatusEnum;
import com.ln.xproject.application.constants.PayStatus;
import com.ln.xproject.application.model.Application;
import com.ln.xproject.application.repository.ApplicationRepository;
import com.ln.xproject.application.service.ApplicationAuditService;
import com.ln.xproject.application.service.ApplicationService;
import com.ln.xproject.application.vo.ApplicationImportVo;
import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.server.PayServer;
import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.job.constants.Constants;
import com.ln.xproject.job.constants.JobMachineStatus;
import com.ln.xproject.job.service.JobService;
import com.ln.xproject.redis.service.RedisService;
import com.ln.xproject.system.model.SysUser;
import com.ln.xproject.system.service.SysUserService;
import com.ln.xproject.util.SerialNoGenerator;
import com.ln.xproject.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ApplicationServiceImpl extends BaseServiceImpl<Application, ApplicationRepository>
        implements ApplicationService {

    @Autowired
    @Override
    protected void setRepository(ApplicationRepository repository) {
        super.repository = repository;
    }

    @Autowired
    private RedisService redisService;
    @Autowired
    private ApplicationAuditService applicationAuditService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private JobService jobService;

    private static final String ASSIGN_LOCK_KEY = "application_lock_key";

    private static final String VERIFY_LOCK_KEY = "application_verify_lock_key";

    @Override
    public List<Application> listByChannelLoanId(String channelLoanId, ApplicationChannel channel) {
        Assert.notNull(channel, "进件渠道");
        Assert.notBlank(channelLoanId, "渠道方进件Id");

        return repository.findByChannelLoanIdAndApplicationChannel(channelLoanId, channel,
                new Sort(Direction.DESC, "id"));
    }

    @Override
    public Application addAppl(ApplicationChannel channel, Long userId, ApplicationImportVo loanVo) {
        Assert.notNull(channel, "进件渠道");
        Assert.notNull(userId, "用户Id");
        Assert.notNull(loanVo, "要保存的进件信息");

        // 校验
        loanVo.checkImport();

        Application appl = this.transToModel(channel, userId, loanVo);
        // 保存
        super.save(appl);
        return appl;
    }

    @Override
    public List<Application> getUnAssignApplication() {
        return this.repository.findByIsInitedAndAuditStatus(Boolean.TRUE.booleanValue(),
                ApplicationAuditStatus.TO_DISTRIBUTE);
    }

    @Override
    public List<Application> listApplToInit() {
        return this.repository.findByIsInited(Boolean.FALSE);
    }

    @Override
    public void initedAppl(Long applId) {
        Assert.notNull(applId, "进件Id");
        Application appl = this.load(applId);
        appl.setIsInited(Boolean.TRUE);
        super.update(appl);
    }

    @Override
    public void updateAuditStatus(Long applicationId, ApplicationAuditStatus applicationAuditStatus,
            Long operatorUserId, ApplicationAuditType applicationAuditType, String remark) {

        if (!redisService.acquireLock(VERIFY_LOCK_KEY + applicationId, 60)) {
            throw ServiceException.exception(CodeConstants.C_10121006);
        }

        try {
            Application application = this.load(applicationId);

            ApplicationAuditStatus.checkAuditStatus(application.getAuditStatus(), applicationAuditStatus);
            application.setAuditStatus(applicationAuditStatus);
            application.setAuditTime(new Date());
            if (applicationAuditStatus == ApplicationAuditStatus.REJECT) {
                application.setFailReason(remark);
            }
            this.update(application);

            // 添加审核日志
            this.applicationAuditService.updateAuditStatus(applicationId, applicationAuditStatus, operatorUserId,
                    applicationAuditType, remark);
        } finally {
            redisService.releaseLock(VERIFY_LOCK_KEY + applicationId);
        }
    }

    private Application transToModel(ApplicationChannel channel, Long userId, ApplicationImportVo loanVo) {
        Application appl = new Application();
        BeanUtils.copyProperties(loanVo, appl);
        appl.setUserId(userId);
        appl.setApplicationChannel(channel);
        appl.setAuditStatus(ApplicationAuditStatus.TO_DISTRIBUTE);
        appl.setPushStatus(ApplicationPushStatus.TO_PUSH);
        appl.setNotityStatus(NoticeStatusEnum.WAIT_PROCESS);
        appl.setPaymentConfirmStatus(PayStatus.WAIT_PROCESS);
        appl.setIsInited(false);
        appl.setVerifySerialNo(SerialNoGenerator.generateSerialNo());
        return appl;
    }

    @Override
    public void saveLoanRecall(JSONObject json) {
        String proxySerialNo = json.getString("businessOrderId");
        String status = json.getString("status");

        Application application = loadByVerifySerialNo(proxySerialNo);
        if (application != null) {
            application.setWeCode(json.getString("weResultStatus"));
            application.setWeMsg(json.getString("weResultMsg"));

            if (PayServer.BussinessStatus.GENERAL_DEAL_SUCCESS.getStatus().equals(status)) {

                // 支付成功
                application.setWeLoanId(json.getString("weLoanId"));
                application.setPaymentConfirmStatus(PayStatus.SUCCESS);
                application.setPushStatus(ApplicationPushStatus.SUCCESS);
                application.setPushTime(new Date());
                application.setVersion(null);
                application.setPaymentConfirmTime(new Date(Long.valueOf(json.getString("resultTime"))));
                this.update(application);
            } else if (PayServer.BussinessStatus.GENERAL_DEAL_FAIL.getStatus().equals(status)) {
                // 支付失败
                application.setPaymentConfirmStatus(PayStatus.FAILED);
                application.setPaymentConfirmTime(new Date());
                application.setFailReason(json.getString("message"));
                if (json.containsKey("weLoanId") && StringUtils.isNotBlank(json.getString("weLoanId"))) {
                    application.setPushStatus(ApplicationPushStatus.SUCCESS);
                    application.setPushTime(new Date());
                    application.setWeLoanId(json.getString("weLoanId"));

                } else {
                    application.setPushStatus(ApplicationPushStatus.FAILED);
                    application.setPushTime(new Date());
                }
                application.setVersion(null);
                this.update(application);
            }
        }
    }

    @Override
    public void applicationAuditManual(Long applicationId, String auditStatus, String auditorEmail, String remark) {
        // 校验auditStatus是否支持
        Assert.notNull(applicationId, "进件Id");
        Assert.notBlank(auditorEmail, "审核人邮箱");
        ApplicationAuditStatus enumAuditStatus = Assert.enumNotValid(ApplicationAuditStatus.class, auditStatus,
                "审核结果状态");

        if (!ApplicationAuditStatus.MANUAL_AUDIT_RESULT.contains(enumAuditStatus)) {
            throw ExceptionUtils.commonError("不支持的审核类型" + auditStatus);
        }

        try {
            // 查询进件信息是否存在
            this.load(applicationId);

            // 验证用户是否存在
            SysUser sysUser = sysUserService.loadByEmail(auditorEmail);

            // 更新进件审核状态，添加审核日志
            updateAuditStatus(applicationId, enumAuditStatus, sysUser.getId(), ApplicationAuditType.MANUAL, remark);

            // 添加job处理终态任务--special状态不做处理
            if (ApplicationAuditStatus.valueOf(auditStatus) == ApplicationAuditStatus.APPROVE) {
                applicationTerminalJob(applicationId, JobMachineStatus.APPL_TO_PUSH);
            } else if (ApplicationAuditStatus.valueOf(auditStatus) == ApplicationAuditStatus.REJECT) {
                applicationTerminalJob(applicationId, JobMachineStatus.APPL_TO_RECALL);
            }
        } catch (ServiceException se) {
            log.error("人工审核失败:status{},messge{}", se.getCode(), se.getMessage());
            throw se;
        } catch (Exception e) {
            log.error("人工审核失败", e);
            throw ExceptionUtils.commonError("人工审核失败");
        }
    }

    private Application loadByVerifySerialNo(String verifySerialNo) {
        Assert.notBlank(verifySerialNo, "审核编号");
        Application appl = repository.findByVerifySerialNo(verifySerialNo);
        Assert.notExist(appl, "进件");
        return appl;
    }

    private void applicationTerminalJob(Long applicationId, JobMachineStatus jobMachineStatus) {
        // 2分45秒+30秒内的随机数
        JSONObject jsonParam = new JSONObject();
        jsonParam.put(Constants.JOB_PARAM_APPLICATION_ID, applicationId);
        jobService.generateJob(jobMachineStatus, applicationId, jsonParam);
    }

}
