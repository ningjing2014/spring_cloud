package com.ln.xproject.application.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ln.xproject.application.model.ApplicationUser;
import com.ln.xproject.application.repository.ApplicationUserRepository;
import com.ln.xproject.application.service.ApplicationUserService;
import com.ln.xproject.application.vo.ApplicationUserImportVo;
import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.service.impl.BaseServiceImpl;
import com.ln.xproject.util.JsonUtils;
import com.ln.xproject.util.UniqueKeyUtils;

@Service
@Transactional
public class ApplicationUserServiceImpl extends BaseServiceImpl<ApplicationUser, ApplicationUserRepository>
        implements ApplicationUserService {

    @Autowired
    @Override
    protected void setRepository(ApplicationUserRepository repository) {
        super.repository = repository;
    }

    @Override
    public Long addApplUser(Long applId, ApplicationUserImportVo userVo) {
        Assert.notNull(userVo, "要保存的进件用户信息");
        // 校验
        userVo.checkImport();

        ApplicationUser user = this.transToModel(applId, userVo);
        // 保存
        super.save(user);
        return user.getId();
    }

    @Override
    public void updateApplUser(Long userId, Long applId) {
        Assert.notNull(userId, "要修改的用户Id");

        ApplicationUser user = this.load(userId);

        if (null != applId) {
            user.setApplicationId(applId);
        }
        super.update(user);
    }

    @Override
    public ApplicationUser loadByApplicationId(Long applId) {
        ApplicationUser entity = this.repository.findByApplicationId(applId);
        Assert.notExist(entity, "进件用户信息");
        return entity;
    }

    private ApplicationUser transToModel(Long applId, ApplicationUserImportVo userVo) {
        ApplicationUser user = new ApplicationUser();
        BeanUtils.copyProperties(userVo, user);
        user.setContactInfo(JsonUtils.toJson(userVo.getContactInfo()));

        if (null == applId) {
            user.setApplicationId(0L);
        } else {
            user.setApplicationId(applId);
        }
        user.setVerifyUserKey(UniqueKeyUtils.uniqueKey());

        return user;
    }

}
