package com.ztq.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author zhengtianqi 2021/7/2
 */
@Component
public class RedisLock {

    private static final Logger logger = LoggerFactory.getLogger(RedisLock.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 加锁
     *
     * @param key        唯一标志
     * @param value      跟唯一标志对应的随机值
     * @param expireTime 过期时间
     * @return
     */
    public boolean lock(String key, String value, long expireTime) {
        //设置30秒的锁,对应setnx命令
        return stringRedisTemplate.opsForValue().setIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
    }


    /**
     * 解锁
     *
     * @param key   唯一标志
     * @param value 跟唯一标志对应的随机值
     */
    public void unlock(String key, String value) {
        try {
            String currentValue = stringRedisTemplate.opsForValue().get(key);
            if (!StringUtils.isEmpty(currentValue) && currentValue.equals(value)) {
                //删除key
                stringRedisTemplate.opsForValue().getOperations().delete(key);
            }
        } catch (Exception e) {
            logger.error("[Redis分布式锁] 解锁出现异常", e);
        }
    }

}
