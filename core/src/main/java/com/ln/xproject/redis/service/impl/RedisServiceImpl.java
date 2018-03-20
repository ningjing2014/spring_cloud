package com.ln.xproject.redis.service.impl;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.ln.xproject.redis.service.RedisService;

@Service
public class RedisServiceImpl implements RedisService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public boolean acquireLock(String lock, long expired) {
        boolean success = false;

        long value = System.currentTimeMillis() + expired * 1000 + 1;

        boolean acquired = redisTemplate.opsForValue().setIfAbsent(lock, String.valueOf(value));
        if (acquired) {
            success = true;
        } else {
            long oldValue = Long.valueOf(redisTemplate.opsForValue().get(lock));

            if (oldValue < System.currentTimeMillis()) {
                String getValue = redisTemplate.opsForValue().getAndSet(lock, String.valueOf(value));
                if (Long.valueOf(getValue) == oldValue) {
                    success = true;
                } else {
                    success = false;
                }
            } else {
                success = false;
            }
        }
        return success;
    }

    @Override
    public void releaseLock(String lock) {
        long current = System.currentTimeMillis();
        if (current < Long.valueOf(redisTemplate.opsForValue().get(lock))) {
            redisTemplate.delete(lock);
        }
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    @Override
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }

    @Override
    public ListOperations<String, String> opsForList() {
        return redisTemplate.opsForList();
    }

    @Override
    public SetOperations<String, String> opsForSet() {
        return redisTemplate.opsForSet();
    }

    @Override
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public void setex(String key, String value, long seconds) {
        redisTemplate.opsForValue().set(key, value, seconds, TimeUnit.SECONDS);
    }

}
