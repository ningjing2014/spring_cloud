package com.ln.xproject.api.service;

import com.ln.xproject.api.vo.ApplicationImportRequestVo;
import com.ln.xproject.api.vo.ApplicationImportResponseVo;
import com.ln.xproject.api.vo.ApplicationStatusRequestVo;
import com.ln.xproject.api.vo.ApplicationStatusResponseVo;

public interface ApplicationApiService {

    /**
     * 进件
     * 
     * @param importRequest
     * @return
     */
    public ApplicationImportResponseVo importAppl(ApplicationImportRequestVo importRequest);

    /**
     * 查询进件状态
     * 
     * @param statusRequest
     * @return
     */
    public ApplicationStatusResponseVo getApplStatus(ApplicationStatusRequestVo statusRequest);

}
