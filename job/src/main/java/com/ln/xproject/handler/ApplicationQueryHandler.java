package com.ln.xproject.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.application.constants.ApplicationChannel;
import com.ln.xproject.application.constants.ApplicationPushStatus;
import com.ln.xproject.application.constants.PayStatus;
import com.ln.xproject.application.model.Application;
import com.ln.xproject.base.server.PayServer;
import com.ln.xproject.job.constants.DbTableStatus;
import com.ln.xproject.job.constants.JobMachineStatus;
import com.ln.xproject.job.model.Job;
import com.ln.xproject.util.JsonUtils;
import com.ln.xproject.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 借款状态查询 （支付系统代付查询）
 *
 * @author ln
 */
@Slf4j
public class ApplicationQueryHandler extends BaseHandler implements Runnable {

    public ApplicationQueryHandler(Job job) {
        super(job);
    }

    public void run() {
        try {
            Long applicationId = JSONObject.parseObject(super.getJob().getJobParam())
                    .getLongValue(com.ln.xproject.job.constants.Constants.JOB_PARAM_APPLICATION_ID);
            Application application = this.getApplicationService().load(applicationId);

            PayStatus payStatus = application.getPaymentConfirmStatus();

            if (PayStatus.SUCCESS == payStatus || PayStatus.FAILED == payStatus) {
                saveJob(DbTableStatus.SUCCESS, null);
                saveNextJob(JobMachineStatus.APPL_TO_RECALL, this.getJob().getJobParam());
                return;
            }

            Map<String, String> params = buildPayQueryParams(ApplicationChannel.UCREDIT.name(),
                    application.getVerifySerialNo());

            MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
            multiValueMap.setAll(params);

            JSONObject result = super.getRestTemplate().postForObject(super.getPayServer().getServerPayQuery(),
                    multiValueMap, JSONObject.class);

            log.info("jobId:" + this.getJob().getId() + ", 请求支付系统代付查询接口, url:{}, params:{}, result:{}",
                    super.getPayServer().getServerPayQuery(), JsonUtils.toJson(params), result);

            if (result != null) {
                String status = result.getString("status");
                if (PayServer.BussinessStatus.SUCCESS.getStatus().equals(status)) {
                    JSONObject payInfoJson = result.getJSONObject("data").getJSONObject("payInfo");
                    String payRstStatus = payInfoJson.getString("status");
                    application.setWeCode(payInfoJson.getString("weResultStatus"));
                    application.setWeMsg(payInfoJson.getString("weResultMsg"));
                    if (PayServer.BussinessStatus.GENERAL_DEAL_SUCCESS.getStatus().equals(payRstStatus)) {
                        application.setWeLoanId(payInfoJson.getString("weLoanId"));
                        application.setPaymentConfirmStatus(PayStatus.SUCCESS);
                        Date confirmDate = new Date(Long.valueOf(
                                result.getJSONObject("data").getJSONObject("payInfo").getString("resultTime")));
                        application.setPaymentConfirmTime(confirmDate);
                        application.setPushStatus(ApplicationPushStatus.SUCCESS);
                        application.setPushTime(new Date());
                        this.getApplicationService().update(application);
                        this.saveJob(DbTableStatus.SUCCESS, null);
                        // 支付成功后执行提现上报
                        this.saveNextJob(JobMachineStatus.APPL_TO_RECALL, this.getJob().getJobParam());
                    } else if (PayServer.BussinessStatus.GENERAL_DEAL_FAIL.getStatus().equals(payRstStatus)) {
                        application.setPaymentConfirmStatus(PayStatus.FAILED);
                        application.setPaymentConfirmTime(new Date());
                        application.setFailReason(result.getString("message"));

                        if (!payInfoJson.containsKey("weLoanId")
                                || StringUtils.isBlank(payInfoJson.getString("weLoanId"))) {
                            application.setPushStatus(ApplicationPushStatus.FAILED);
                            application.setPushTime(new Date());
                            application.setFailReason("推标失败：" + application.getWeMsg());
                        } else {
                            application.setPushStatus(ApplicationPushStatus.SUCCESS);
                            application.setPushTime(new Date());
                            application.setWeLoanId(payInfoJson.getString("weLoanId"));
                        }

                        this.getApplicationService().update(application);
                        this.saveJob(DbTableStatus.SUCCESS, result.getString("message"));

                        this.saveNextJob(JobMachineStatus.APPL_TO_RECALL, this.getJob().getJobParam());
                    } else {
                        if (payInfoJson.containsKey("weLoanId")
                                && StringUtils.isNotBlank(payInfoJson.getString("weLoanId"))) {
                            if (application.getPushStatus() != ApplicationPushStatus.SUCCESS) {
                                application.setPushStatus(ApplicationPushStatus.SUCCESS);
                                application.setPushTime(new Date());
                                application.setWeLoanId(payInfoJson.getString("weLoanId"));
                                this.saveNextJob(JobMachineStatus.APPL_TO_RECALL, this.getJob().getJobParam());
                            }
                        }
                        application.setPaymentConfirmStatus(PayStatus.PROCESSING);
                        application.setPaymentConfirmTime(new Date());
                        this.getApplicationService().update(application);
                        this.saveJob(DbTableStatus.INIT, result.getString("支付处理中"));
                    }
                } else {
                    // 该错误码应该不会出现
                    this.saveJob(DbTableStatus.INIT, result.getString("message"));
                }
            } else {
                this.saveJob(DbTableStatus.INIT, "请求未得到结果");
            }
        } catch (Exception e) {
            log.error("执行job失败. jobId:" + super.getJob().getId(), e);
        }
    }

    public static Map<String, String> buildPayQueryParams(String partnerCode, String businessOrderId) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("businessType", partnerCode);
        params.put("businessOrderId", businessOrderId);
        params.put("sign", PayServer.getSignOfPay(params));
        return params;
    }
}
