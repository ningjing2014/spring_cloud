package com.ln.xproject.application.service;

import com.ln.xproject.application.model.ApplicationCardInfo;
import com.ln.xproject.application.vo.ApplicationCardImportVo;
import com.ln.xproject.base.service.BaseService;

public interface ApplicationCardInfoService extends BaseService<ApplicationCardInfo> {

    /**
     * 保存
     * 
     * @param applId
     * @param bankCardVo
     */
    void addApplBankCard(Long applId, ApplicationCardImportVo bankCardVo);

    ApplicationCardInfo loadByApplicationId(Long applId);

}
