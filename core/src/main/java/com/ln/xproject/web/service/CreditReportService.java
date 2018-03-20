package com.ln.xproject.web.service;

import com.ln.xproject.application.model.CreditReport;
import com.ln.xproject.application.vo.credit.CreditDetailVo;

public interface CreditReportService {

    /**
     * 保存征信报告
     * 
     * @param creditVo
     */
    void addCreditReport(CreditDetailVo creditVo);

    /**
     * 央行征信信息查询
     * 
     * @param applicationNo
     * @return
     */
    CreditDetailVo creditDetail(Long applicationNo);

    CreditReport getByApplicationNo(Long applicationNo);

}
