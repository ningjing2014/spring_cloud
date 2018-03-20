package com.ln.xproject.handler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;

import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.api.vo.ApplicationResultResponseVo;
import com.ln.xproject.application.constants.ApplicationAuditStatus;
import com.ln.xproject.application.constants.ApplicationPushStatus;
import com.ln.xproject.application.constants.NoticeStatusEnum;
import com.ln.xproject.application.model.Application;
import com.ln.xproject.application.model.ApplicationAudit;
import com.ln.xproject.base.code.Code;
import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.job.constants.Constants;
import com.ln.xproject.job.constants.DbTableStatus;
import com.ln.xproject.job.model.Job;
import com.ln.xproject.util.HttpUtil;
import com.ln.xproject.util.JsonUtils;
import com.ln.xproject.util.SerialNoGenerator;
import com.ln.xproject.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ApplicationRecallHandler extends BaseHandler implements Runnable {

    private static final String RECALL_SUCCESS_STATUS = "1";

    public ApplicationRecallHandler(Job job) {
        super(job);
    }

    @Override
    public void run() {
        try {
            Long applicationId = JSONObject.parseObject(super.getJob().getJobParam())
                    .getLongValue(Constants.JOB_PARAM_APPLICATION_ID);
            Application application = this.getApplicationService().load(applicationId);

            String requestJson = this.buildNoticeParam(application);
            String notifyUrl = application.getNotifyUrl();

            HttpUtil httpUtil = new HttpUtil();
            Map<String, String> map = new HashMap<>();
            String userName = super.getUcreditServer().getCallBackUser();
            String pwd = super.getUcreditServer().getCallBackPassword();
            map.put("Authorization", "Basic " + Base64.encodeBase64String((userName + ":" + pwd).getBytes()));
            String rstStr = httpUtil.post(notifyUrl, requestJson, map);
            log.info("jobId:{},url:{} ,args:{},rst:{} ", getJob().getId(), notifyUrl, requestJson, rstStr);
            JSONObject result = JSONObject.parseObject(rstStr);
            if (result != null) {
                String retCode = result.getJSONObject("meta").getString("code");
                if (RECALL_SUCCESS_STATUS.equals(retCode)) {
                    application.setNotifyTime(new Date());
                    application.setNotityStatus(NoticeStatusEnum.PROCESS_SUCCESS);
                    this.getApplicationService().update(application);

                    this.saveJob(DbTableStatus.SUCCESS, null);
                } else {
                    this.saveJob(DbTableStatus.INIT, result.toJSONString());
                }
            } else {
                this.saveJob(DbTableStatus.INIT, "请求未得到结果");
            }

        } catch (Exception e) {
            log.error("执行job出错. jobId:" + super.getJob().getId(), e);
            this.saveJob(DbTableStatus.INIT, "请求未得到结果");
        }

    }

    private String buildNoticeParam(Application application) {
        ApplicationResultResponseVo vo = new ApplicationResultResponseVo();
        ApplicationResultResponseVo.Head head = new ApplicationResultResponseVo.Head();
        head.setReqBizCode("query_verify_loan_result");
        head.setReqSeqNo(SerialNoGenerator.generateSerialNo());
        vo.setHead(head);
        vo.setLendId(application.getChannelLoanId());
        Code verifyCode = getVerifyCode(application.getAuditStatus());
        vo.setVerifyCode(verifyCode.getCode());
        ApplicationAudit applicationAudit = this.getApplicationAuditService().loadByApplicationId(application.getId());
        vo.setVerifyMsg(StringUtils.isBlank(applicationAudit.getRejectReason()) ? verifyCode.getMessage()
                : applicationAudit.getRejectReason());
        vo.setWeCode(application.getWeCode());
        vo.setWeMsg(application.getWeMsg());
        Code pushCode = getPushCode(application.getPushStatus());
        vo.setPushCode(pushCode.getCode());
        vo.setPushMsg(pushCode.getMessage());
        return JsonUtils.toJson(vo);
    }

    private Code getVerifyCode(ApplicationAuditStatus auditResult) {
        if (ApplicationAuditStatus.APPROVE == auditResult) {
            return CodeConstants.C_000000;
        } else if (ApplicationAuditStatus.REJECT == auditResult) {
            return CodeConstants.C_10121000;
        } else {
            return CodeConstants.C_000001;
        }
    }

    private Code getPushCode(ApplicationPushStatus pushStatus) {
        if (ApplicationPushStatus.SUCCESS == pushStatus) {
            return CodeConstants.C_000000;
        } else if (ApplicationPushStatus.FAILED == pushStatus) {
            return CodeConstants.C_10121004;
        } else {
            return CodeConstants.C_000001;
        }
    }
}
