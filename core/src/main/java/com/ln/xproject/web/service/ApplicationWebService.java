package com.ln.xproject.web.service;

import com.ln.xproject.api.vo.ApplicationListRequestVo;
import com.ln.xproject.application.vo.ApplicationDetailVo;
import com.ln.xproject.application.vo.ApplicationListVo;
import com.ln.xproject.base.vo.PageVo;

public interface ApplicationWebService {

    /**
     * 进件列表
     * 
     * @param userId
     * @param listRequest
     * @param pageSize
     * @param pageNum
     * @return
     */
    PageVo<ApplicationListVo> listAppl(Long userId, ApplicationListRequestVo listRequest, Integer pageNum,
            Integer pageSize);

    /**
     * 获取进件详情
     * 
     * @param applicationId
     * @return
     */
    ApplicationDetailVo getApplicationDetail(Long applicationId);
}
