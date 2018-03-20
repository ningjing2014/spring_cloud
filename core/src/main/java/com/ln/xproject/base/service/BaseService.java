package com.ln.xproject.base.service;

import java.util.List;

import org.springframework.data.domain.Sort;

import com.ln.xproject.base.model.BaseModel;

public interface BaseService<T extends BaseModel> {

    T get(Long id);

    T load(Long id);

    List<T> findAll();

    List<T> findAll(Sort sort);

    public T save(T entity);

    public T update(T entity);

}
