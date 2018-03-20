package com.ln.xproject.application.repository;

import com.ln.xproject.application.model.ApplicationCardInfo;
import com.ln.xproject.base.repository.BaseRepository;

public interface ApplicationCardInfoRepository extends BaseRepository<ApplicationCardInfo> {
    ApplicationCardInfo findByApplicationId(Long applicationId);

}
