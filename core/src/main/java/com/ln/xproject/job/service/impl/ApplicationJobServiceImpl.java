package com.ln.xproject.job.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.util.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.application.model.Application;
import com.ln.xproject.application.model.ApplicationUser;
import com.ln.xproject.application.service.ApplicationService;
import com.ln.xproject.application.service.ApplicationUserService;
import com.ln.xproject.application.vo.credit.AttentioninfoVo;
import com.ln.xproject.application.vo.credit.CreditDetailVo;
import com.ln.xproject.application.vo.credit.CreditInfoVo;
import com.ln.xproject.application.vo.credit.CustomerInfoVo;
import com.ln.xproject.application.vo.credit.GuaranteeInfoVo;
import com.ln.xproject.application.vo.credit.HisOverdueInfoVo;
import com.ln.xproject.application.vo.credit.LiabilityInfoVo;
import com.ln.xproject.application.vo.credit.NegativeInfoVo;
import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.ExceptionUtils;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.server.UcreditServer;
import com.ln.xproject.job.service.ApplicationJobService;
import com.ln.xproject.job.vo.CreditReportQueryResultVo;
import com.ln.xproject.redis.service.RedisService;
import com.ln.xproject.util.HttpUtil;
import com.ln.xproject.util.JsonUtils;
import com.ln.xproject.web.service.CreditReportService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class ApplicationJobServiceImpl implements ApplicationJobService {

    private static final String TOKEN_REDIS_LOCK_KEY = "TOKEN_REDIS_LOCK_KEY";
    private static final Long TOKEN_REDIS_LOCK_EXPIRE = new Long(10 * 60);

    private static final String CREDIT_TOKEN_REDIS_KEY = "CREDIT_TOKEN_REDIS_KEY";
    private static final Long CREDIT_TOKEN_REDIS_EXPIRE = new Long(8 * 60);

    private static final String TOKEN_INVALID_MSG = "token信息验证失败";
    private static final String EMPTY_CREDIT_MSG = "无该系统来源的人行数据";

    @Value("${not_care_credit_info}")
    private boolean notCareCreditInfo;

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private ApplicationUserService applicationUserService;
    @Autowired
    private CreditReportService creditReportService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private UcreditServer ucreditServer;

    @Override
    public void initAppl(Long applId) {
        Assert.notNull(applId, "进件Id");
        try {
            // 拉取征信信息
            CreditReportQueryResultVo resultVo = this.queryCreditReport(applId);

            if (resultVo.getResult() == CreditReportQueryResultVo.QueryResult.RETRY) {
                return;
            } else if (resultVo.getResult() == CreditReportQueryResultVo.QueryResult.PASS) {
                // 初始化完成
                applicationService.initedAppl(applId);
                return;
            } else if (resultVo.getResult() == CreditReportQueryResultVo.QueryResult.SUCCESS) {
                Assert.notNull(resultVo.getResultJson(), "征信信息");
            }

            // 解析结果
            CreditDetailVo creditReport = parseToVo(applId, resultVo.getResultJson());
            // 保存征信信息
            creditReportService.addCreditReport(creditReport);
            // 初始化完成
            applicationService.initedAppl(applId);
        } catch (Exception e) {
            if (notCareCreditInfo) {
                log.error("获取进件{}的征信信息异常，将放弃征信，继续后续流程", applId, e);
                // 初始化完成
                applicationService.initedAppl(applId);
            } else {
                log.error("获取进件{}的征信信息异常", applId, e);
            }
        }
    }

    private CreditReportQueryResultVo queryCreditReport(Long applId) {
        // 加锁，保证请求token和请求征信信息的事务性
        if (!redisService.acquireLock(TOKEN_REDIS_LOCK_KEY, TOKEN_REDIS_LOCK_EXPIRE)) {
            throw ServiceException.exception(CodeConstants.C_10121006);
        }

        try {
            Application appl = applicationService.load(applId);
            ApplicationUser user = applicationUserService.loadByApplicationId(applId);

            // 获取token
            String token = this.getToken();

            Map<String, String> header = new HashMap<String, String>();
            header.put("Authorization", token);
            header.put("Accept", "text/html");

            Map<String, String> params = new HashMap<String, String>();
            HttpUtil httpUtil = new HttpUtil();
            // url及参数
            String url = String.format(ucreditServer.getReportQueryUrl(), appl.getChannelLoanId(), user.getIdCard());
            // 请求友信
            String result = httpUtil.post(url, JsonUtils.toJson(params), header);

            CreditReportQueryResultVo resultVo = new CreditReportQueryResultVo();
            log.info("查询征信信息，url:{}, token:{}, result:{}", url, token, result);

            if (StringUtils.isNotBlank(result)) {
                JSONObject resultJson = JSONObject.parseObject(result);
                JSONObject operation = resultJson.getJSONObject("operation");
                String state = operation.getString("operationState");

                if (state.equals(UcreditServer.ResponseStatus.SUCCESS.getStatus())) {
                    JSONObject value = resultJson.getJSONObject("value");
                    resultVo.setResult(CreditReportQueryResultVo.QueryResult.SUCCESS);
                    resultVo.setResultJson(value);
                    return resultVo;
                } else {
                    String message = operation.getString("operationMsg");

                    if (state.equals(UcreditServer.ResponseStatus.AUTHENTICATE_FAIL.getStatus())
                            && message.equals(TOKEN_INVALID_MSG)) {
                        log.info("请求友信查询进件{}的征信信息时，返回state:{},可能token失效，将刷新token，等待下次任务重新请求", applId, state);
                        // 刷新token
                        this.refreshToken();
                        resultVo.setResult(CreditReportQueryResultVo.QueryResult.RETRY);
                        resultVo.setResultJson(null);
                        return resultVo;
                    } else if (state.equals(UcreditServer.ResponseStatus.BUSINESS_FAIL.getStatus())
                            && message.equals(EMPTY_CREDIT_MSG)) {
                        log.info("进件{}不存在征信信息", applId);
                        resultVo.setResult(CreditReportQueryResultVo.QueryResult.PASS);
                        resultVo.setResultJson(null);
                        return resultVo;
                    }
                    log.error("请求友信查询进件{}的征信信息返回失败，state:{}, message:{}", applId, state, message);
                    throw ExceptionUtils.commonError("请求友信查询征信信息失败");
                }
            } else {
                log.error("请求友信查询进件{}的征信信息未收到响应", applId);
                throw ExceptionUtils.commonError("请求友信查询征信信息未收到响应");
            }
        } finally {
            // 释放锁
            redisService.releaseLock(TOKEN_REDIS_LOCK_KEY);
        }
    }

    /*
     * 解析数据到Vo
     */
    private CreditDetailVo parseToVo(Long applId, JSONObject result) {
        CreditDetailVo detail = new CreditDetailVo();

        JSONObject report = result.getJSONObject("report");
        Assert.notNull(report, "征信报告用户信息");

        CustomerInfoVo customerVo = new CustomerInfoVo();
        customerVo.setCustName(report.getString("name"));
        customerVo.setGender(report.getString("gender"));
        customerVo.setIdNo(report.getString("idNo"));
        customerVo.setIdType(report.getString("idType"));
        customerVo.setReportTime(report.getString("reportTime"));
        customerVo.setSearchTime(report.getString("searchTime"));

        List<GuaranteeInfoVo> guaranteeList = new ArrayList<GuaranteeInfoVo>();
        JSONArray guaranteeArray = result.getJSONArray("creditForPersons");

        if (null != guaranteeArray && guaranteeArray.size() > 0) {
            for (int i = 0; i < guaranteeArray.size(); i++) {
                JSONObject guaranteeJo = guaranteeArray.getJSONObject(i);
                GuaranteeInfoVo guaranteeVo = new GuaranteeInfoVo();
                guaranteeVo.setBankName(guaranteeJo.getString("bankName"));
                guaranteeVo.setCardNum(guaranteeJo.getString("cardNum"));
                guaranteeVo.setCardType(guaranteeJo.getString("cardType"));
                guaranteeVo.setDealDate(guaranteeJo.getString("dealDate"));
                guaranteeVo.setForPersonName(guaranteeJo.getString("forPersonName"));
                guaranteeVo.setGuaranteeAmount(guaranteeJo.getString("guaranteeAmount"));
                guaranteeVo.setGuaranteeBalance(guaranteeJo.getString("guaranteeBalance"));
                guaranteeVo.setGuaranteeContractAmount(guaranteeJo.getString("guaranteeContractAmount"));
                guaranteeVo.setGuaranteeType(guaranteeJo.getString("guaranteeType"));
                guaranteeVo.setInfoDate(guaranteeJo.getString("infoDate"));
                guaranteeList.add(guaranteeVo);
            }
        }
        List<NegativeInfoVo> negativeInfoList = new ArrayList<NegativeInfoVo>();
        JSONArray negativeArray = result.getJSONArray("creditForcedExecutes");

        if (null != negativeArray && negativeArray.size() > 0) {
            for (int i = 0; i < negativeArray.size(); i++) {
                JSONObject negativeJo = negativeArray.getJSONObject(i);
                NegativeInfoVo negativeVo = new NegativeInfoVo();
                negativeVo.setAlreadyExecute(negativeJo.getString("alreadyExecute"));
                negativeVo.setAlreadyExecuteAmount(negativeJo.getString("alreadyExecuteAmount"));
                negativeVo.setApplyExecute(negativeJo.getString("applyExecute"));
                negativeVo.setApplyExecuteAmount(negativeJo.getString("applyExecuteAmount"));
                negativeVo.setCaseNo(negativeJo.getString("caseNo"));
                negativeVo.setCourt(negativeJo.getString("court"));
                negativeVo.setFilDate(negativeJo.getString("filDate"));
                negativeVo.setOverDate(negativeJo.getString("overDate"));
                negativeVo.setOverWay(negativeJo.getString("overWay"));
                negativeVo.setReason(negativeJo.getString("reason"));
                negativeVo.setReportId(negativeJo.getString("reportId"));
                negativeVo.setStatus(negativeJo.getString("status"));
                negativeInfoList.add(negativeVo);
            }
        }
        JSONObject calResult = result.getJSONObject("calculationResult");

        CreditInfoVo creditVo = new CreditInfoVo();
        creditVo.setCardCredit6MAvgPayment(calResult.getString("cardCredit6MAvgPayment"));
        creditVo.setCardCreditBalance(calResult.getString("cardCreditBalance"));
        creditVo.setCardCreditMax(calResult.getString("cardCreditMax"));
        creditVo.setCardCreditMin(calResult.getString("cardCreditMin"));
        creditVo.setCardCreditTotal(calResult.getString("cardCreditTotal"));
        creditVo.setCreditCardCount(calResult.getString("creditCardCount"));
        creditVo.setDefaultCardCount(calResult.getString("defaultCardCount"));
        creditVo.setDefaultCardMaxAmount(calResult.getString("defaultCardMaxAmount"));
        creditVo.setDefaultCardMaxDur(calResult.getString("defaultCardMaxDur"));
        creditVo.setDefaultCardMonthCount(calResult.getString("defaultCardMonthCount"));
        creditVo.setDefaultLoanCount(calResult.getString("defaultLoanCount"));
        creditVo.setDefaultLoanMaxAmount(calResult.getString("defaultLoanMaxAmount"));
        creditVo.setDefaultLoanMaxDur(calResult.getString("defaultLoanMaxDur"));
        creditVo.setDefaultLoanMonthCount(calResult.getString("defaultLoanMonthCount"));
        creditVo.setFirstCardDate(calResult.getString("firstCardDate"));
        creditVo.setFirstLoanDate(calResult.getString("firstLoanDate"));
        creditVo.setHouseLoanCount(calResult.getString("houseLoanCount"));
        creditVo.setOsLoan6MAvgPayment(calResult.getString("osLoan6MAvgPayment"));
        creditVo.setOsLoanBalance(calResult.getString("osLoanBalance"));
        creditVo.setOsLoanTotal(calResult.getString("osLoanTotal"));
        creditVo.setOtherLoanCount(calResult.getString("otherLoanCount"));

        HisOverdueInfoVo hisOverdueVo = new HisOverdueInfoVo();
        hisOverdueVo.setAllDefaultMonthWithin6MCount(calResult.getString("allDefaultMonthWithin6MCount"));
        hisOverdueVo.setAllDefaultWithin6MCount(calResult.getString("allDefaultWithin6MCount"));
        hisOverdueVo.setAllEffectiveDaysMax(calResult.getString("allEffectiveDaysMax"));
        hisOverdueVo.setBusinessLoanEffectiveDaysMax(calResult.getString("businessLoanEffectiveDaysMax"));
        hisOverdueVo.setCardCountDf90pIn5Year(calResult.getString("cardCountDf90pIn5Year"));
        hisOverdueVo.setCollateralLoanEffectiveDaysMax(calResult.getString("collateralLoanEffectiveDaysMax"));
        hisOverdueVo.setCreditCardDefaultMonthWithin6MCount(calResult.getString("creditCardDefaultMonthWithin6MCount"));
        hisOverdueVo.setCreditCardDefaultWithin6MCount(calResult.getString("creditCardDefaultWithin6MCount"));
        hisOverdueVo.setCreditLoanEffectiveDaysMax(calResult.getString("creditLoanEffectiveDaysMax"));
        hisOverdueVo.setHousingLoanCountDf90pIn5Year(calResult.getString("housingLoanCountDf90pIn5Year"));
        hisOverdueVo.setHousingLoanEffectiveDaysMax(calResult.getString("housingLoanEffectiveDaysMax"));
        hisOverdueVo.setLoanDefaultMonthWithin6MCount(calResult.getString("loanDefaultMonthWithin6MCount"));
        hisOverdueVo.setLoanDefaultWithin6MCount(calResult.getString("loanDefaultWithin6MCount"));
        hisOverdueVo.setOtherLoanCountDf90pIn5Year(calResult.getString("otherLoanCountDf90pIn5Year"));
        hisOverdueVo.setOtherPlatLoanSituation(calResult.getString("otherPlatLoanSituation"));

        LiabilityInfoVo liabilityVo = new LiabilityInfoVo();
        liabilityVo.setBusinessLoanActiveBalanceSum(calResult.getString("businessLoanActiveBalanceSum"));
        liabilityVo.setBusinessLoanActiveDuePaymentSum(calResult.getString("businessLoanActiveDuePaymentSum"));
        liabilityVo.setBusinessLoanActiveTotalAmountSum(calResult.getString("businessLoanActiveTotalAmountSum"));
        liabilityVo.setCollateralLoanActiveBalanceSum(calResult.getString("collateralLoanActiveBalanceSum"));
        liabilityVo.setCollateralLoanActiveDuePaymentSum(calResult.getString("collateralLoanActiveDuePaymentSum"));
        liabilityVo.setCollateralLoanActiveTotalAmountSum(calResult.getString("collateralLoanActiveTotalAmountSum"));
        liabilityVo.setCreditCardActiveActualPaymentSum(calResult.getString("creditCardActiveActualPaymentSum"));
        liabilityVo.setCreditCardActiveBalanceSum(calResult.getString("creditCardActiveBalanceSum"));
        liabilityVo.setCreditCardActiveDuePaymentSum(calResult.getString("creditCardActiveDuePaymentSum"));
        liabilityVo.setCreditLoanActiveBalanceSum(calResult.getString("creditLoanActiveBalanceSum"));
        liabilityVo.setCreditLoanActiveDuePaymentSum(calResult.getString("creditLoanActiveDuePaymentSum"));
        liabilityVo.setCreditLoanActiveTotalAmountSum(calResult.getString("creditLoanActiveTotalAmountSum"));
        liabilityVo.setHousingLoanActiveBalanceSum(calResult.getString("housingLoanActiveBalanceSum"));
        liabilityVo.setHousingLoanActiveDuePaymentSum(calResult.getString("housingLoanActiveDuePaymentSum"));
        liabilityVo.setHousingLoanActiveTotalAmountSum(calResult.getString("housingLoanActiveTotalAmountSum"));
        liabilityVo.setLoanActiveBalanceSum(calResult.getString("loanActiveBalanceSum"));
        liabilityVo.setLoanActiveDuePaymentSum(calResult.getString("loanActiveDuePaymentSum"));
        liabilityVo.setLoanActiveTotalAmountSum(calResult.getString("loanActiveTotalAmountSum"));

        AttentioninfoVo attentionVo = new AttentioninfoVo();
        attentionVo.setAllActivatedwithin3MCount(calResult.getString("allActivatedWithin3MCount"));
        attentionVo.setCreditCardActivatedWithin3MCount(calResult.getString("creditCardActivatedWithin3MCount"));
        attentionVo.setQueryAsCardApplyWithin3MCount(calResult.getString("queryAsCardApplyWithin3MCount"));
        attentionVo.setQueryAsLoanApplyWithin3MCount(calResult.getString("queryAsLoanApplyWithin3MCount"));
        attentionVo.setQueryWithin3MCount(calResult.getString("queryWithin3MCount"));

        detail.setApplicationNo(applId.toString());
        detail.setCustomerInfo(customerVo);
        detail.setCreditInfo(creditVo);
        detail.setGuaranteeInfo(guaranteeList);
        detail.setNegativeInfo(negativeInfoList);
        detail.setHisOverdueInfo(hisOverdueVo);
        detail.setLiabilityInfo(liabilityVo);
        detail.setAttentioninfo(attentionVo);

        return detail;
    }

    private String getToken() {
        // 从缓存中取
        String token = redisService.get(CREDIT_TOKEN_REDIS_KEY);

        if (StringUtils.isNotBlank(token)) {
            return token;
        }
        // 请求token并写入缓存
        token = this.refreshToken();
        return token;
    }

    /*
     * 刷新缓存中的token
     */
    private String refreshToken() {
        // 从培训贷请求token
        String token = requestToken();

        redisService.setex(CREDIT_TOKEN_REDIS_KEY, token, CREDIT_TOKEN_REDIS_EXPIRE);
        return token;
    }

    private String requestToken() {
        String token = "";
        String url = ucreditServer.getTokenQueryUrl();

        HttpUtil httpClient = new HttpUtil();
        // 请求token
        String result = httpClient.post(url, new HashMap<String, String>());

        log.info("查询征信token，result:{}", result);

        if (StringUtils.isBlank(result)) {
            log.info("调用征信系统获取令牌接口，未得到响应结果");
            throw ExceptionUtils.commonError("未能获取到令牌");
        }
        JSONObject response = JSONObject.parseObject(result);
        JSONObject operation = response.getJSONObject("operation");
        String state = operation.getString("operationState");

        if (state.equals(UcreditServer.ResponseStatus.SUCCESS.getStatus())) {
            token = response.getString("value");
        } else {
            String message = operation.getString("operationMsg");
            log.error("查询征信token返回失败，state:{}, message:{}", state, message);
            throw ExceptionUtils.commonError("请求友信查询token失败");
        }
        if (StringUtils.isBlank(token)) {
            log.info("查询征信token返回token为空");
            throw ExceptionUtils.commonError("获取token失败");
        }
        log.info("请求征信系统获取token接口, token: {}", token);
        return token;
    }

}
