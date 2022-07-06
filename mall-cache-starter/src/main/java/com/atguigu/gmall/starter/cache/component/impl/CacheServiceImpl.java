package com.atguigu.gmall.starter.cache.component.impl;

import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author Kingstu
 * @date 2022/6/27 20:41
 */
@Service
public class CacheServiceImpl implements CacheService {

    @Autowired
    StringRedisTemplate redisTemplate;

    ScheduledExecutorService delayExecutor = Executors.newScheduledThreadPool(4);


    @Override
    public <T> T getData(String cacheKey, Class<T> t) {
        String json = redisTemplate.opsForValue().get(cacheKey);
        T obj = JSONs.toObj(json, t);
        return obj;
    }

    @Override
    public <T> T getData(String cacheKey, TypeReference<T> t) {
        String json = redisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        T obj = JSONs.toObj(json, t);
        return obj;
    }

    @Override
    public <T> void saveData(String cacheKey, T detail) {
        redisTemplate.opsForValue().set(cacheKey, JSONs.toStr(detail));
    }

    @Override
    public <T> void saveData(String cacheKey, T detail, Long time, TimeUnit unit) {
        redisTemplate.opsForValue().set(cacheKey, JSONs.toStr(detail),time,unit);
    }

    @Override
    public void delayDoubleDelete(String key) {
        //1.立即删
        redisTemplate.delete(key);

        //2.延迟删
        delayExecutor.schedule(()->{
            redisTemplate.delete(key);
        }, 10, TimeUnit.SECONDS);

    }
}
