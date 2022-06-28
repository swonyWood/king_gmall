package com.atguigu.gmall.item.component;

import com.atguigu.gmall.model.vo.SkuDetailVo;

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
