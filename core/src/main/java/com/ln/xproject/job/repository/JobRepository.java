package com.ln.xproject.job.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.job.model.Job;

public interface JobRepository extends BaseRepository<Job> {
    Page<Job> findByJobStatusAndIdGreaterThan(Byte jobStatus, Long id, Pageable pageable);
}
