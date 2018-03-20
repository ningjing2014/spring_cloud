package com.ln.xproject.base.repository;

import org.springframework.data.domain.Page;

import com.ln.xproject.base.config.CostomPageRequest;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;

public interface SpecialRepository<T> {

    void detach(Object obj);

    /**
     * @param query
     * @param pageable
     *            排序不好用，需要OrderSpecifier的参数单传
     * @param o
     *            排序必传
     * @return
     */
    Page<T> findAll(JPAQuery query, CostomPageRequest pageable, OrderSpecifier... o);
}
