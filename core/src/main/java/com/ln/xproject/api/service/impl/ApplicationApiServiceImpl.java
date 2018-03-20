package com.ln.xproject.api.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.ln.xproject.api.service.ApplicationApiService;
import com.ln.xproject.api.vo.ApplicationImportRequestVo;
import com.ln.xproject.api.vo.ApplicationImportResponseVo;
import com.ln.xproject.api.vo.ApplicationStatusRequestVo;
import com.ln.xproject.api.vo.ApplicationStatusResponseVo;
import com.ln.xproject.application.constants.ApplicationAuditStatus;
import com.ln.xproject.application.constants.ApplicationChannel;
import com.ln.xproject.application.constants.ApplicationPushStatus;
import com.ln.xproject.application.constants.PayStatus;
import com.ln.xproject.application.model.Application;
import com.ln.xproject.application.service.ApplicationAuditService;
import com.ln.xproject.application.service.ApplicationCardInfoService;
import com.ln.xproject.application.service.ApplicationService;
import com.ln.xproject.application.service.ApplicationUserService;
import com.ln.xproject.application.vo.ApplicationCardImportVo;
import com.ln.xproject.application.vo.ApplicationImportVo;
import com.ln.xproject.application.vo.ApplicationUserImportVo;
import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ServiceException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ApplicationApiServiceImpl implements ApplicationApiService {

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationUserService applicationUserService;
    @Autowired
    private ApplicationCardInfoService applicationCardInfoService;
    @Autowired
    private ApplicationAuditService applicationAuditService;

    @Override
    public ApplicationImportResponseVo importAppl(ApplicationImportRequestVo importRequest) {
        // 校验
        this.checkImport(importRequest);

        ApplicationChannel channel = ApplicationChannel.valueOf(importRequest.getPartner());
        ApplicationImportVo loan = importRequest.getLoan();
        ApplicationUserImportVo user = importRequest.getUser();
        ApplicationCardImportVo bank = importRequest.getBank();

        // 保存用户信息
        Long userId = applicationUserService.addApplUser(null, user);
        // 保存进件信息
        Application appl = applicationService.addAppl(channel, userId, loan);
        Long applId = appl.getId();
        String channelLoanId = appl.getChannelLoanId();

        // 用户信息补充进件Id
        applicationUserService.updateApplUser(userId, applId);

        // 保存银行卡信息
        applicationCardInfoService.addApplBankCard(applId, bank);

        // 保存审核信息
        applicationAuditService.addAudit(applId);

        log.info("进件成功，channel:{}, channelLoanId:{}, applicationId:{}", channel.name(), channelLoanId, applId);

        ApplicationImportResponseVo repsonseVo = new ApplicationImportResponseVo();
        repsonseVo.setApplicationId(appl.getVerifySerialNo());
        repsonseVo.setLoanRefId(channelLoanId);
        return repsonseVo;
    }

    @Override
    public ApplicationStatusResponseVo getApplStatus(ApplicationStatusRequestVo statusRequest) {
        Assert.notNull(statusRequest, "请求信息");
        Assert.notBlank(statusRequest.getChannelLoanId(), "渠道方借款Id");
        ApplicationChannel channel = Assert.enumNotValid(ApplicationChannel.class, statusRequest.getPartner(), "进件渠道");
        String channelLoanId = statusRequest.getChannelLoanId();

        return getApplStatus(channel, channelLoanId);
    }

    private ApplicationStatusResponseVo getApplStatus(ApplicationChannel channel, String channelLoanId) {
        List<Application> applList = applicationService.listByChannelLoanId(channelLoanId, channel);
        Assert.notEmpty(applList, "进件");

        // 接口返回最新一条进件的状态
        Application appl = applList.get(0);
        Assert.notExist(appl, "进件");

        ApplicationStatusResponseVo statusVo = new ApplicationStatusResponseVo();

        statusVo.setApplicationId(appl.getVerifySerialNo());
        statusVo.setLoanRefId(channelLoanId);
        statusVo.setAuditStatus(appl.getAuditStatus().name());
        statusVo.setPushStatus(appl.getPushStatus().name());
        statusVo.setGrantStatus(appl.getPaymentConfirmStatus().name());

        if (null != appl.getAuditTime()) {
            statusVo.setAuditTime(String.valueOf(appl.getAuditTime().getTime()));
        }
        if (null != appl.getPushTime()) {
            statusVo.setPushTime(String.valueOf(appl.getPushTime().getTime()));
        }
        if (null != appl.getPaymentConfirmTime()) {
            statusVo.setGrantTime(String.valueOf(appl.getPaymentConfirmTime().getTime()));
        }
        String failReason = appl.getFailReason();

        if (appl.getAuditStatus() == ApplicationAuditStatus.REJECT) {
            statusVo.setAuditRejectReason(failReason);
        }
        if (appl.getPushStatus() == ApplicationPushStatus.FAILED) {
            if (StringUtils.isBlank(failReason)) {
                failReason = appl.getWeMsg();
            }
            statusVo.setGrantFailReason(failReason);
        }
        if (appl.getPaymentConfirmStatus() == PayStatus.FAILED) {
            if (StringUtils.isBlank(failReason)) {
                failReason = appl.getWeMsg();
            }
            statusVo.setGrantFailReason(failReason);
        }
        statusVo.setWeCode(appl.getWeCode());
        statusVo.setWeMsg(appl.getWeMsg());
        return statusVo;
    }

    /*
     * 校验进件参数
     */
    private void checkImport(ApplicationImportRequestVo importRequest) {
        Assert.notNull(importRequest, "进件请求信息");

        String channel = importRequest.getPartner();
        ApplicationImportVo loan = importRequest.getLoan();
        ApplicationUserImportVo user = importRequest.getUser();
        ApplicationCardImportVo bank = importRequest.getBank();

        Assert.notNull(loan, "进件信息");
        Assert.notNull(user, "进件用户信息");
        Assert.notNull(bank, "进件银行卡信息");
        ApplicationChannel enumApplChannel = Assert.enumNotValid(ApplicationChannel.class, channel, "进件渠道");

        // 校验进件信息
        loan.checkImport();
        // 校验用户信息
        user.checkImport();
        // 校验银行卡信息
        bank.checkImport();

        // 校验重复进件
        if (this.isApplRepeat(loan.getChannelLoanId(), enumApplChannel)) {
            throw ServiceException.exception(CodeConstants.C_10121002);
        }
    }

    /*
     * 判断进件是否重复进件
     */
    private boolean isApplRepeat(String channelLoanId, ApplicationChannel channel) {
        List<Application> applList = applicationService.listByChannelLoanId(channelLoanId, channel);

        if (CollectionUtils.isEmpty(applList)) {
            return false;
        }
        // 过滤出其中未失败的进件
        applList = applList.stream().filter(appl -> (appl.getAuditStatus() != ApplicationAuditStatus.REJECT
                && appl.getPushStatus() != ApplicationPushStatus.FAILED)).collect(Collectors.toList());

        // 存在未失败的进件，则重复
        if (!CollectionUtils.isEmpty(applList)) {
            return true;
        } else {
            return false;
        }
    }
}
