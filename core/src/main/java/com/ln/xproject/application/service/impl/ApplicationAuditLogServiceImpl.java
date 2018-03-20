package com.ln.xproject.application.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.ln.xproject.application.model.Application;
import com.ln.xproject.application.service.ApplicationService;
import com.ln.xproject.application.vo.ApplicationAuditLogListVo;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.application.model.ApplicationAuditLog;
import com.ln.xproject.application.repository.ApplicationAuditLogRepository;
import com.ln.xproject.application.service.ApplicationAuditLogService;
import com.ln.xproject.system.model.SysUser;
import com.ln.xproject.system.service.SysUserService;
import com.ln.xproject.util.DateUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ApplicationAuditLogServiceImpl extends BaseServiceImpl<ApplicationAuditLog, ApplicationAuditLogRepository>
        implements ApplicationAuditLogService {

    @Autowired
    ApplicationService applicationService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    @Override
    protected void setRepository(ApplicationAuditLogRepository repository) {
        super.repository = repository;
    }

    @Override
    public List<ApplicationAuditLogListVo> getByApplicationNo(Long applicationId) {
        Assert.notNull(applicationId, "进件id");

        Application application = applicationService.get(applicationId);
        Assert.notExist(application, "进件信息不存在");

        List<ApplicationAuditLog> applicationAuditLogList = this.findAll(applicationId);
        if (CollectionUtils.isEmpty(applicationAuditLogList)) {
            log.info("进件号 {} 审核日志信息为空", applicationId);
            return Collections.emptyList();
        }

        List<ApplicationAuditLogListVo> rst = new ArrayList<ApplicationAuditLogListVo>();

        applicationAuditLogList.forEach(rcd -> rst.add(transferToVo(rcd)));

        return rst;
    }

    private ApplicationAuditLogListVo transferToVo(ApplicationAuditLog applicationAuditLog) {
        ApplicationAuditLogListVo vo = new ApplicationAuditLogListVo();
        if (applicationAuditLog.getOperatorUserId() == null) {
            vo.setAuditorName(null);
        } else {
            SysUser sysUser = sysUserService.load(applicationAuditLog.getOperatorUserId());
            vo.setAuditorName(sysUser == null ? null : sysUser.getRealName());
        }
        vo.setFromAuditStatus(applicationAuditLog.getFromAuditResult().name());
        vo.setToAuditStatus(applicationAuditLog.getToAuditResult().name());
        vo.setOperateTime(DateUtils.format(applicationAuditLog.getAuditTime(), DateUtils.DATE_TIME));
        return vo;
    }

    @Override
    public List<ApplicationAuditLog> findAll(Long applicationId) {
        return repository.findByApplicationId(applicationId);
    }
}
