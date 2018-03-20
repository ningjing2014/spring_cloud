package com.ln.xproject.application.service;

import com.ln.xproject.application.model.ApplicationUser;
import com.ln.xproject.application.vo.ApplicationUserImportVo;
import com.ln.xproject.base.service.BaseService;

public interface ApplicationUserService extends BaseService<ApplicationUser> {

    /**
     * 保存
     * 
     * @param applId
     * @param user
     * @return
     */
    Long addApplUser(Long applId, ApplicationUserImportVo user);

    /**
     * 修改用户
     * 
     * @param userId
     * @param applId
     */
    void updateApplUser(Long userId, Long applId);

    /**
     * 根据进件ID查询对应用户信息
     * 
     * @param applId
     * @return
     */
    ApplicationUser loadByApplicationId(Long applId);

}
