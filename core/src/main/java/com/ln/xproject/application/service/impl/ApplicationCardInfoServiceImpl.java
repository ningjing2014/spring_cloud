package com.ln.xproject.application.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ln.xproject.application.model.ApplicationCardInfo;
import com.ln.xproject.application.repository.ApplicationCardInfoRepository;
import com.ln.xproject.application.service.ApplicationCardInfoService;
import com.ln.xproject.application.vo.ApplicationCardImportVo;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.service.impl.BaseServiceImpl;

@Service
@Transactional
public class ApplicationCardInfoServiceImpl extends BaseServiceImpl<ApplicationCardInfo, ApplicationCardInfoRepository>
        implements ApplicationCardInfoService {

    @Autowired
    @Override
    protected void setRepository(ApplicationCardInfoRepository repository) {
        super.repository = repository;
    }

    @Override
    public void addApplBankCard(Long applId, ApplicationCardImportVo bankCardVo) {
        Assert.notNull(applId, "进件Id");
        Assert.notNull(bankCardVo, "要保存的进件银行卡信息");
        // 校验
        bankCardVo.checkImport();

        ApplicationCardInfo cardInfo = this.transToModel(applId, bankCardVo);
        // 保存
        super.save(cardInfo);
    }

    @Override
    public ApplicationCardInfo loadByApplicationId(Long applId) {
        ApplicationCardInfo entity = this.repository.findByApplicationId(applId);
        Assert.notExist(entity, "进件用户信息");
        return entity;
    }

    private ApplicationCardInfo transToModel(Long applId, ApplicationCardImportVo bankCardVo) {
        ApplicationCardInfo cardInfo = new ApplicationCardInfo();
        BeanUtils.copyProperties(bankCardVo, cardInfo);
        cardInfo.setApplicationId(applId);
        return cardInfo;
    }

}
