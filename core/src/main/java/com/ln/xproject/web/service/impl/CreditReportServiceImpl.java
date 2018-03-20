package com.ln.xproject.web.service.impl;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ln.xproject.application.model.CreditReport;
import com.ln.xproject.application.repository.CreditReportRepository;
import com.ln.xproject.application.service.ApplicationService;
import com.ln.xproject.application.vo.credit.AttentioninfoVo;
import com.ln.xproject.application.vo.credit.CreditDetailVo;
import com.ln.xproject.application.vo.credit.CreditInfoVo;
import com.ln.xproject.application.vo.credit.CustomerInfoVo;
import com.ln.xproject.application.vo.credit.GuaranteeInfoVo;
import com.ln.xproject.application.vo.credit.HisOverdueInfoVo;
import com.ln.xproject.application.vo.credit.LiabilityInfoVo;
import com.ln.xproject.application.vo.credit.NegativeInfoVo;
import com.ln.xproject.base.code.CodeConstants;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.exception.ServiceException;
import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.util.DateUtils;
import com.ln.xproject.util.JsonUtils;
import com.ln.xproject.web.service.CreditReportService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class CreditReportServiceImpl extends BaseServiceImpl<CreditReport, CreditReportRepository>
        implements CreditReportService {

    @Autowired
    ApplicationService applicationService;

    @Autowired
    @Override
    protected void setRepository(CreditReportRepository repository) {
        super.repository = repository;
    }

    @Override
    public void addCreditReport(CreditDetailVo creditVo) {
        // 校验
        this.checkCreditReport(creditVo);
        CreditReport report = this.transToModel(creditVo);
        super.save(report);
    }

    @Override
    public CreditDetailVo creditDetail(Long applicationId) {
        Assert.notNull(applicationId, "进件id");

        applicationService.load(applicationId);

        CreditReport creditReport = this.getByApplicationNo(applicationId);
        Assert.notExist(creditReport, "央行征信信息");

        CreditDetailVo creditDetailVo = this.transTo(creditReport);
        return creditDetailVo;
    }

    @Override
    public CreditReport getByApplicationNo(Long applicationNo) {
        Assert.notNull(applicationNo, "进件编号");
        List<CreditReport> list = repository.findByApplicationId(applicationNo);
        if (CollectionUtils.isEmpty(list)) {
            return null;
        } else {
            return list.get(0);
        }
    }

    private void checkCreditReport(CreditDetailVo creditVo) {
        Assert.notNull(creditVo, "要保存的证信信息");
        Assert.notBlank(creditVo.getApplicationNo(), "所属进件号");
        Assert.notNull(creditVo.getCustomerInfo(), "征信客户信息");

        CustomerInfoVo customerInfo = creditVo.getCustomerInfo();
        Assert.notBlank(customerInfo.getCustName(), "客户姓名");
        Assert.notBlank(customerInfo.getGender(), "性别");
        Assert.notBlank(customerInfo.getIdNo(), "身份证号");
        Assert.notBlank(customerInfo.getIdType(), "证件类型");
        Assert.notBlank(customerInfo.getReportTime(), "报告时间");
        Assert.notBlank(customerInfo.getSearchTime(), "查询时间");
    }

    private CreditDetailVo transTo(CreditReport creditReport) {
        CreditDetailVo creditDetailVo = new CreditDetailVo();

        creditDetailVo.setApplicationNo(creditReport.getApplicationId().toString());

        CustomerInfoVo customerInfoVo = new CustomerInfoVo();
        customerInfoVo.setCustName(creditReport.getCustName());
        customerInfoVo.setIdNo(creditReport.getIdNo());
        customerInfoVo.setGender(creditReport.getGender());
        customerInfoVo.setIdType(creditReport.getIdType());
        customerInfoVo.setReportTime(String.valueOf(creditReport.getReportTime().getTime()));
        customerInfoVo.setSearchTime(String.valueOf(creditReport.getSearchTime().getTime()));
        creditDetailVo.setCustomerInfo(customerInfoVo);

        List<GuaranteeInfoVo> guaranteeInfoVo = jsonToList(creditReport.getGuaranteeInfo(), GuaranteeInfoVo.class,
                "担保信息");
        creditDetailVo.setGuaranteeInfo(guaranteeInfoVo);

        List<NegativeInfoVo> negativeInfoVo = jsonToList(creditReport.getNegativeInfo(), NegativeInfoVo.class, "负面信息");
        creditDetailVo.setNegativeInfo(negativeInfoVo);

        CreditInfoVo creditInfoVo = jsonToObject(creditReport.getCreditInfo(), CreditInfoVo.class, "信用总体信息");
        creditDetailVo.setCreditInfo(creditInfoVo);

        HisOverdueInfoVo hisOverdueInfoVo = jsonToObject(creditReport.getHisOverdue(), HisOverdueInfoVo.class,
                "历史和逾期信息");
        hisOverdueInfoVo.setOtherPlatLoanSituation("暂未获取"); // 经产品确认传默认值
        creditDetailVo.setHisOverdueInfo(hisOverdueInfoVo);

        LiabilityInfoVo liabilityInfoVo = jsonToObject(creditReport.getLiabilityInfo(), LiabilityInfoVo.class, "负债信息");
        creditDetailVo.setLiabilityInfo(liabilityInfoVo);

        AttentioninfoVo attentioninfoVo = jsonToObject(creditReport.getAttentionInfo(), AttentioninfoVo.class,
                "关切程度信息");
        creditDetailVo.setAttentioninfo(attentioninfoVo);

        return creditDetailVo;
    }

    /**
     * JSON 转 POJO
     */
    private <T> T jsonToObject(String pojo, Class<T> tclass, String message) {
        try {
            return JSONObject.parseObject(pojo, tclass);
        } catch (Exception e) {
            log.error("央行征信 - {} json转换失败", message, e);
            throw ServiceException.exception(CodeConstants.C_10101022, message);
        }
    }

    /**
     * json 转 List<T>
     */
    public <T> List<T> jsonToList(String jsonString, Class<T> clazz, String message) {
        try {
            List<T> ts = (List<T>) JSONArray.parseArray(jsonString, clazz);
            return ts;
        } catch (Exception e) {
            log.error("央行征信 - {} json转换失败", message, e);
            throw ServiceException.exception(CodeConstants.C_10101022, message);
        }
    }

    private CreditReport transToModel(CreditDetailVo creditVo) {
        CreditReport report = new CreditReport();

        CustomerInfoVo customerInfo = creditVo.getCustomerInfo();

        report.setApplicationId(Long.valueOf(creditVo.getApplicationNo()));
        report.setCustName(customerInfo.getCustName());
        report.setGender(customerInfo.getGender());
        report.setIdType(customerInfo.getIdType());
        report.setIdNo(customerInfo.getIdNo());
        report.setReportTime(DateUtils.parse(customerInfo.getReportTime(), DateUtils.DATE_TIME));
        report.setSearchTime(DateUtils.parse(customerInfo.getSearchTime(), DateUtils.DATE_TIME));

        if (null != creditVo.getCreditInfo()) {
            report.setCreditInfo(JsonUtils.toJson(creditVo.getCreditInfo()));
        }
        if (CollectionUtils.isEmpty(creditVo.getGuaranteeInfo())) {
            report.setGuaranteeInfo(JsonUtils.toJson(creditVo.getGuaranteeInfo()));
        }
        if (CollectionUtils.isEmpty(creditVo.getNegativeInfo())) {
            report.setNegativeInfo(JsonUtils.toJson(creditVo.getNegativeInfo()));
        }
        if (null != creditVo.getHisOverdueInfo()) {
            report.setHisOverdue(JsonUtils.toJson(creditVo.getHisOverdueInfo()));
        }
        if (null != creditVo.getLiabilityInfo()) {
            report.setLiabilityInfo(JsonUtils.toJson(creditVo.getLiabilityInfo()));
        }
        if (null != creditVo.getAttentioninfo()) {
            report.setAttentionInfo(JsonUtils.toJson(creditVo.getAttentioninfo()));
        }
        return report;
    }
}
