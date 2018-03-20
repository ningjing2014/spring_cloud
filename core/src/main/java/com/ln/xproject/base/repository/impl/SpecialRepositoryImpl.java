package com.ln.xproject.base.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Repository;

import com.ln.xproject.base.config.CostomPageRequest;
import com.ln.xproject.base.repository.SpecialRepository;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;

@Repository
public class SpecialRepositoryImpl<T> implements SpecialRepository<T> {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void detach(Object obj) {
        if (obj == null) {
            return;
        }
        entityManager.detach(obj);
    }

    @Override
    public Page<T> findAll(JPAQuery query, CostomPageRequest pageable, OrderSpecifier... o) {
        long count = query.fetchCount();
        if (pageable != null) {
            query.offset((long) pageable.getOffset());
            query.limit((long) pageable.getPageSize());
        }
        if (o != null && o.length > 0) {
            query.orderBy(o);
        }
        return new PageImpl(query.fetchResults().getResults(), pageable, count);
    }
}
