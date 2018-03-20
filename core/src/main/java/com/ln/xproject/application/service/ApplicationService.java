package com.ln.xproject.application.service;

import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.application.constants.ApplicationAuditStatus;
import com.ln.xproject.application.constants.ApplicationAuditType;
import com.ln.xproject.application.constants.ApplicationChannel;
import com.ln.xproject.application.model.Application;
import com.ln.xproject.application.vo.ApplicationImportVo;
import com.ln.xproject.base.service.BaseService;

public interface ApplicationService extends BaseService<Application> {

    /**
     * 按渠道和渠道方进件Id查询
     * 
     * @param channelLoanId
     * @param channel
     * @return
     */
    List<Application> listByChannelLoanId(String channelLoanId, ApplicationChannel channel);

    /**
     * 保存进件
     * 
     * @param channel
     * @param userId
     * @param loanVo
     * @return
     */
    Application addAppl(ApplicationChannel channel, Long userId, ApplicationImportVo loanVo);

    List<Application> getUnAssignApplication();

    void updateAuditStatus(Long applicationId, ApplicationAuditStatus applicationAuditStatus, Long operatorUserId,

            ApplicationAuditType applicationAuditType, String remark);

    void saveLoanRecall(JSONObject json);

    /**
     * 信审系统人工审核进件信息
     * 
     * @param applicationId
     *            审核系统进件id
     * @param auditStatus
     *            审核状态
     * @param auditorId
     *            操作人id
     * @param remark
     *            备注
     */
    void applicationAuditManual(Long applicationId, String auditStatus, String auditorId, String remark);

    /**
     * 查询所有需要初始化的进件
     * 
     * @return
     */
    List<Application> listApplToInit();

    /**
     * 设置进件为完成初始化
     * 
     * @param applId
     */
    void initedAppl(Long applId);
}
