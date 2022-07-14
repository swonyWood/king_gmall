package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/13 21:17
 */
public interface SeckillGoodsCacheService {


    /**
     * 预热秒杀商品到缓存
     * @param day
     * @param goods
     */
    void saveToCache(String day, List<SeckillGoods> goods);

    /**
     * 查询缓存中当天参与秒杀的数据
     * @param day
     * @return
     */
    List<SeckillGoods> getCachedSeckillGoods(String day);

    /**
     * 获取某个秒杀商品详情
     * @param skuId
     * @return
     */
    SeckillGoods getSeckillGood(Long skuId);
}
