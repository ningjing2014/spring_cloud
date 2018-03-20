package com.ln.xproject.application.service;

import java.util.List;

import com.ln.xproject.application.constants.ApplicationAuditStatus;
import com.ln.xproject.application.constants.ApplicationAuditType;
import com.ln.xproject.application.model.ApplicationAudit;
import com.ln.xproject.base.service.BaseService;

public interface ApplicationAuditService extends BaseService<ApplicationAudit> {

    /**
     * 保存进件审核
     * 
     * @param applId
     */
    void addAudit(Long applId);

    /**
     * 根据进件ID来查询审核记录
     * 
     * @return
     */
    ApplicationAudit loadByApplicationId(Long applicationId);

    void assign(Long applicationId, Long userId);

    void generateAudoVerifyJob(Long applicationId);

    void updateAuditStatus(Long applicationId, ApplicationAuditStatus applicationAuditStatus, Long operatorUserId,
            ApplicationAuditType applicationAuditType, String remark);

    Boolean isDistribute(String applicationId, Long sysUserId);

    List<ApplicationAudit> loadByOperatorUserId(Long userId);
    /**
     * 根据进件ID来查询审核记录,不返回错误信息
     *
     * @return
     */
    ApplicationAudit loadByApplicationIdWithoutException(Long applicationId);

}
