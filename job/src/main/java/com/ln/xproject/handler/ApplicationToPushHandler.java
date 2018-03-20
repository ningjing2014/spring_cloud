package com.ln.xproject.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.application.constants.ApplicationChannel;
import com.ln.xproject.application.constants.PayStatus;
import com.ln.xproject.application.model.Application;
import com.ln.xproject.application.model.ApplicationCardInfo;
import com.ln.xproject.application.model.ApplicationUser;
import com.ln.xproject.base.server.PayServer;
import com.ln.xproject.job.constants.Constants;
import com.ln.xproject.job.constants.DbTableStatus;
import com.ln.xproject.job.constants.JobMachineStatus;
import com.ln.xproject.job.model.Job;
import com.ln.xproject.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationToPushHandler extends BaseHandler implements Runnable {

    public ApplicationToPushHandler(Job job) {
        super(job);
    }

    @Override
    public void run() {
        Long applicationId = JSONObject.parseObject(super.getJob().getJobParam())
                .getLongValue(Constants.JOB_PARAM_APPLICATION_ID);

        Application application = this.getApplicationService().load(applicationId);

        Map<String, String> params = buildPayParams(applicationId, null);
        MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.setAll(params);
        JSONObject result = super.getRestTemplate().postForObject(super.getPayServer().getServerPay(), multiValueMap,
                JSONObject.class);
        log.info("jobId:" + this.getJob().getId() + ", 请求支付系统代付接口, url:{}, params:{}, result:{}",
                super.getPayServer().getServerPay(), params, result);
        if (result != null) {
            String status = result.getString("status");
            if (PayServer.BussinessStatus.SUCCESS.getStatus().equals(status)) {
                JSONObject payInfoJson = result.getJSONObject("data").getJSONObject("payInfo");
                String payOrderNo = payInfoJson.getString("orderId");
                application.setPaySerialNo(payOrderNo);
                application.setPaymentConfirmStatus(PayStatus.PROCESSING);
                application.setPaymentConfirmTime(new Date());
                super.getApplicationService().update(application);
                saveJob(DbTableStatus.SUCCESS, "");
                saveNextJob(JobMachineStatus.APPL_TO_QUERY, getJob().getJobParam());
            } else {
                if (PayServer.BussinessStatus.ERROR_REPEAT.getStatus().equals(status)) {
                    // 订单重复
                    super.saveJob(DbTableStatus.SUCCESS, "");
                    super.saveNextJob(JobMachineStatus.APPL_TO_QUERY, this.getJob().getJobParam());
                } else {
                    String message = result.getString("message");
                    application.setFailReason(message);
                    super.getApplicationService().update(application);
                    super.saveJob(DbTableStatus.NOTICE_MANUAL, message);
                }
            }
        } else {
            // 未得到返回结果，重置
            super.saveJob(DbTableStatus.INIT, "请求未得到结果");
        }
    }

    /**
     * 借款参数, 支付系统待付接口
     *
     * @param applicationId
     * @return
     */
    public Map<String, String> buildPayParams(Long applicationId, String recallPath) {
        Application application = getApplicationService().load(applicationId);
        ApplicationUser applicationUser = this.getApplicationUserService().loadByApplicationId(applicationId);
        ApplicationCardInfo applicationCardInfo = this.getApplicationCardInfoService()
                .loadByApplicationId(applicationId);
        Map<String, String> params = new HashMap<String, String>();
        params.put("bankCardNo", applicationCardInfo.getBankCardNo());
        params.put("idCardNumber", applicationUser.getIdCard());
        params.put("idCardName", applicationUser.getRealName());
        params.put("mobile", StringUtils.splitToList(applicationUser.getMobile(), ".").get(0));
        params.put("businessType", ApplicationChannel.UCREDIT.name());
        params.put("businessOrderId", application.getVerifySerialNo());
        params.put("userKey", applicationUser.getVerifyUserKey());
        params.put("amount", application.getAmount().toString());

        JSONObject requestParamJson = new JSONObject();
        JSONObject loanInfoVo = buildLoanParam(application);
        JSONObject bankInfoVo = buildBankParam(applicationUser, applicationCardInfo);
        JSONObject userInfoVo = buildUserParam(applicationUser, application);

        requestParamJson.put("loan", loanInfoVo);
        requestParamJson.put("user", userInfoVo);
        requestParamJson.put("bank", bankInfoVo);
        params.put("params", requestParamJson.toJSONString());

        params.put("payChannel", ApplicationChannel.UCREDIT.getPayChannel());
        params.put("paySignedCompany", ApplicationChannel.UCREDIT.getPaySubject());
        if (StringUtils.isNotBlank(recallPath)) {
            params.put("callbackUrl", recallPath);
        }
        params.put("sign", PayServer.getSignOfPay(params));
        return params;
    }

    private JSONObject buildBankParam(ApplicationUser applicationUser, ApplicationCardInfo applicationCardInfo) {
        JSONObject param = new JSONObject();
        param.put("bankCde", applicationCardInfo.getBankCode());
        param.put("province", applicationCardInfo.getProvince());
        param.put("city", applicationCardInfo.getCity());
        param.put("name", applicationUser.getRealName());
        param.put("bankName", applicationCardInfo.getBankName());
        param.put("bankAddress", applicationCardInfo.getBankAddress());
        param.put("cardNumber", applicationCardInfo.getBankCardNo());
        return param;
    }

    private JSONObject buildLoanParam(Application application) {
        JSONObject param = new JSONObject();
        param.put("amount", application.getAmount());
        param.put("months", application.getPeriods());
        param.put("interest", application.getInterest());
        param.put("description", application.getDescription());
        param.put("borrowType", application.getBorrowType());
        param.put("refID", application.getChannelLoanId());
        param.put("title", application.getTitle());
        param.put("givenServiceFee", application.getServiceFee());

        param.put("tid", application.getTicketId());

        return param;

    }

    private JSONObject buildUserParam(ApplicationUser applicationUser, Application application) {
        JSONObject param = new JSONObject();
        param.put("jobStatus", applicationUser.getJobStatus());
        param.put("otherRelationTel", applicationUser.getOtherRelationTel());
        // TODO要确认
        param.put("gender", applicationUser.getGender());
        param.put("idCard", applicationUser.getIdCard());
        param.put("companyName", applicationUser.getCompanyName());
        param.put("immediateRelationShip", applicationUser.getImmediateRelationShip());
        param.put("otherRelationName", applicationUser.getOtherRelationName());
        param.put("companyPost", applicationUser.getCompanyPost());
        param.put("homeTownCity", applicationUser.getHomeTownCity());
        param.put("password", applicationUser.getPassword());
        param.put("accountLocationProvince", applicationUser.getAccountLocationProvince());
        param.put("graduation", applicationUser.getGraduation());
        param.put("otherRelationShip", applicationUser.getOtherRelationShip());
        param.put("immediateTel", applicationUser.getImmediateTel());
        param.put("residence", applicationUser.getResidence());
        param.put("companyProvince", applicationUser.getCompanyProvince());
        param.put("hasCar", applicationUser.getHasCar());
        param.put("monthlyIncome", applicationUser.getMonthlyIncome());
        param.put("companyCity", applicationUser.getCompanyCity());
        param.put("homeTownProvince", applicationUser.getHomeTownProvince());
        param.put("currentUnitLife", applicationUser.getCurrentUnitLife());
        param.put("nickName", applicationUser.getNickName());
        param.put("companyTel", applicationUser.getCompanyTel());
        param.put("immediateName", applicationUser.getImmediateName());
        param.put("mobile", applicationUser.getMobile());
        param.put("birth", applicationUser.getBirth());
        param.put("companyCategory", applicationUser.getCompanyCategory());
        param.put("hasHouseLoan", applicationUser.getHasHouseLoan());
        param.put("marriageStatus", applicationUser.getMarriageStatus());
        param.put("accountLocationCity", applicationUser.getAccountLocationCity());
        param.put("companySize", applicationUser.getCompanySize());
        param.put("userName", applicationUser.getUserEmail());
        param.put("officeEmail", applicationUser.getOfficeEmail());
        param.put("realName", applicationUser.getRealName());
        param.put("companyAddress", applicationUser.getCompanyAddress());
        param.put("hasCarLoan", applicationUser.getHasCarLoan());
        param.put("hasChild", applicationUser.getHasChild());
        param.put("hasHouse", applicationUser.getHasHouse());
        param.put("companyIndustry", applicationUser.getCompanyIndustry());
        param.put("refID", applicationUser.getUserRefId());
        param.put("newFlow", application.getNewFlow());
        return param;

    }

}
