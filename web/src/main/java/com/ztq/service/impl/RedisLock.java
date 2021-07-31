package com.ztq.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author zhengtianqi 2021/7/2
 * 分布式中我们对redis的序列化要采用一致的方式呦:StringRedisTemplate、RedisTemplate两个template千万不要操作相同的Key
 */
@Component
public class RedisLock {

    private static final Logger logger = LoggerFactory.getLogger(RedisLock.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * Redis加锁的操作
     *
     * @param key   键
     * @param value 值
     * @return 布尔类型
     */
    public Boolean tryLock(String key, String value) {
        /**
         * 获取尝试时间
         */
        long expireTime = 5 * 10000;
        if (stringRedisTemplate.opsForValue().setIfAbsent(key, value, expireTime, TimeUnit.SECONDS)) {
            return true;
        }
        String currentValue = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotEmpty(currentValue) && Long.parseLong(currentValue) < System.currentTimeMillis()) {
            //获取上一个锁的时间 如果高并发的情况可能会出现已经被修改的问题  所以多一次判断保证线程的安全
            String oldValue = stringRedisTemplate.opsForValue().getAndSet(key, value);
            return StringUtils.isNotEmpty(oldValue) && oldValue.equals(currentValue);
        }
        return false;
    }

    /**
     * Redis解锁的操作
     *
     * @param key   键
     * @param value 值
     */
    public void unlock(String key, String value) {
        String currentValue = stringRedisTemplate.opsForValue().get(key);
        try {
            if (StringUtils.isNotEmpty(currentValue) && currentValue.equals(value)) {
                stringRedisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Exception e) {
        }

    }
}
