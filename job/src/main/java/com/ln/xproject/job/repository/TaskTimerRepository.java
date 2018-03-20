package com.ln.xproject.job.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.job.constants.TaskStatus;
import com.ln.xproject.job.model.TaskTimer;

public interface TaskTimerRepository extends BaseRepository<TaskTimer> {

    TaskTimer findByTaskClass(String taskClass);

    List<TaskTimer> findByTaskStatus(TaskStatus taskStatus);

    Page<TaskTimer> findByTaskStatus(TaskStatus taskStatus, Pageable pageable);

    Page<TaskTimer> findByTaskClass(String taskClass, Pageable pageable);

    Page<TaskTimer> findByTaskClassAndTaskStatus(String taskClass, TaskStatus taskStatus, Pageable pageable);

    List<TaskTimer> findByTaskClassAndTaskStatus(String taskClass, TaskStatus taskStatus);

}
