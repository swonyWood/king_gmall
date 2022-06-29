package com.atguigu.gmall.starter.cache.component;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.concurrent.TimeUnit;

/**
 * @author Kingstu
 * @date 2022/6/27 20:41
 */
public interface CacheService {
    /**
     * 从缓存中获取数据
     * @param cacheKey
     * @param
     * @return
     */
    <T> T getData(String cacheKey, Class<T> t);

    /**
     * 从缓存中获取数据,复杂类型
     * @param cacheKey
     * @param t
     * @param <T>
     * @return
     */
    <T> T getData(String cacheKey, TypeReference<T> t);

    /**
     * 给缓存保存一个数据
     * @param cacheKey
     * @param detail
     * @param <T>
     */
    <T> void saveData(String cacheKey, T detail);

    /**
     * 给缓存保存一个数据,带存活时间
     * @param cacheKey
     * @param detail
     * @param time
     * @param unit
     * @param <T>
     */
    <T> void saveData(String cacheKey, T detail, Long time, TimeUnit unit);
}
