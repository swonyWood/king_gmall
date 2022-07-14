package com.atguigu.gmall.seckill.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.seckill.service.SeckillGoodsCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Kingstu
 * @date 2022/7/13 21:17
 */
@Slf4j
@Service
public class SeckillGoodsCacheServiceImpl implements SeckillGoodsCacheService {


    @Autowired
    StringRedisTemplate redisTemplate;

    //秒杀商品的本地缓存
    Map<Long,SeckillGoods> localCache = new ConcurrentHashMap<>();

    //多级缓存
    @Override
    public void saveToCache(String day, List<SeckillGoods> goods) {

        String key = RedisConst.SECKILL_GOODS_CACHE_PREFIX+day;
        //1.给redis存一份
        goods.stream().forEach(item->{
            //双缓存写入
            //保存到缓存中
            redisTemplate.opsForHash().put(key, item.getSkuId().toString(), JSONs.toStr(item));
            localCache.put(item.getSkuId(), item);
        });

        //设置过期时间
        redisTemplate.expire(key, 2, TimeUnit.DAYS);

    }

    @Override
    public List<SeckillGoods> getCachedSeckillGoods(String day) {
        //1.先查本地缓存
        List<SeckillGoods> goods = localCache.values().stream()
                .sorted((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime()))
                .collect(Collectors.toList());

        log.info("秒杀本地缓存命中数据:{}",goods.size());
        if (goods == null || goods.size() == 0) {
            //本地缓存没有再查redis
            HashOperations<String, String, String> ops = redisTemplate.opsForHash();
            List<String> json = ops.values(RedisConst.SECKILL_GOODS_CACHE_PREFIX + day);
            goods = json.stream().map(str -> JSONs.toObj(str, SeckillGoods.class))
                    .sorted((o1, o2) -> o1.getStartTime().compareTo(o2.getStartTime()))
                    .collect(Collectors.toList());
            log.info("秒杀分布式缓存命中数据:{}",goods.size());
            //3.返回本地缓存
            goods.stream().forEach(item->localCache.put(item.getSkuId(), item));

        }

        return goods;
    }

    @Override
    public SeckillGoods getSeckillGood(Long skuId) {

        SeckillGoods goods = localCache.get(skuId);
        if (goods == null) {
            //看下是否是宕机导致
            if(localCache.size()==0){
                //本地缓存没数据,就可能是宕机了,本地同步了
                getCachedSeckillGoods(DateUtil.formatDate(new Date()));
                goods = localCache.get(skuId);
                return goods;
            }
        }
        return goods;
    }
}
