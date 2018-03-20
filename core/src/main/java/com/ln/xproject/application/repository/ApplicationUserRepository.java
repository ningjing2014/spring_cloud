package com.ln.xproject.application.repository;

import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.application.model.ApplicationUser;

public interface ApplicationUserRepository extends BaseRepository<ApplicationUser> {
    ApplicationUser findByApplicationId(Long applicationId);
}
