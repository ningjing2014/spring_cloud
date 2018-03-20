package com.ln.xproject.base.service.impl;

import java.util.Date;
import java.util.List;

import com.ln.xproject.base.repository.SpecialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;

import com.ln.xproject.base.exception.Assert;
import com.ln.xproject.base.model.BaseModel;
import com.ln.xproject.base.repository.BaseRepository;
import com.ln.xproject.base.service.BaseService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class BaseServiceImpl<T extends BaseModel, R extends BaseRepository<T>> implements BaseService<T> {

    protected R repository;

    protected abstract void setRepository(R repository);

    @PersistenceContext
    protected EntityManager entityManager;
    @Autowired
    protected SpecialRepository specialRepository;

    public T save(T entity) {
        Assert.notNull(entity, "对象");

        Date date = new Date();

        entity.setCreateTime(date);
        entity.setUpdateTime(date);
        T t = repository.save(entity);
        entity.setId(t.getId());
        return t;
    }

    public T update(T entity) {
        Assert.notNull(entity, "对象");
        Assert.notNull(entity.getId(), "主键");

        Date date = new Date();

        entity.setUpdateTime(date);

        return repository.save(entity);
    }

    @Override
    public T get(Long id) {
        Assert.notNull(id, "主键");
        return repository.findOne(id);
    }

    @Override
    public T load(Long id) {
        T entity = get(id);
        Assert.notExist(entity, "对象");
        return entity;
    }

    @Override
    public List<T> findAll() {
        return repository.findAll();
    }

    @Override
    public List<T> findAll(Sort sort) {
        return repository.findAll(sort);
    }

}
