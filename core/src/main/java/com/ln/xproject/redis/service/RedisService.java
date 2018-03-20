package com.ln.xproject.redis.service;

import java.util.Set;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;

public interface RedisService {

    /**
     * 获取锁
     * 
     * @param lock
     * @param expired
     *            单位：秒
     * @return
     */
    boolean acquireLock(String lock, long expired);

    /**
     * 释放锁
     * 
     * @param lock
     */
    void releaseLock(String lock);

    /**
     * exists
     * 
     * @param key
     * @return
     */
    boolean exists(String key);

    /**
     * set
     * 
     * @param key
     * @param value
     */
    void set(String key, String value);

    /**
     * delete
     * 
     * @param key
     */
    void delete(String key);

    /**
     * keys
     * 
     * @param pattern
     * @return
     */
    Set<String> keys(String pattern);

    ListOperations<String, String> opsForList();

    SetOperations<String, String> opsForSet();

    String get(String key);

    void setex(String key, String value, long seconds);

}
