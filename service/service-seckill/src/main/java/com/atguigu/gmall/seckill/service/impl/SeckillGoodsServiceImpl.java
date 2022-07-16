package com.atguigu.gmall.seckill.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.seckill.service.SeckillGoodsCacheService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 *
 */
@Slf4j
@Service
public class SeckillGoodsServiceImpl extends ServiceImpl<SeckillGoodsMapper, SeckillGoods>
    implements SeckillGoodsService{

    @Autowired
    SeckillGoodsMapper seckillGoodsMapper;

    @Autowired
    SeckillGoodsCacheService seckillGoodsCacheService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Override
    public List<SeckillGoods> getCurrentDaySeckillGoods() {
        //1.今天参与秒杀的所有商品
        String currentDay = DateUtil.formatDate(new Date());

        //2.查询指定日期的商品
        return getDaySeckillGoodsFromCache(currentDay);
    }

    @Override
    public List<SeckillGoods> getDaySeckillGoodsFromCache(String day) {

        //先从缓存中获取
        List<SeckillGoods> cached = seckillGoodsCacheService.getCachedSeckillGoods(day);
        return cached;
    }

    @Override
    public List<SeckillGoods> getDaySeckillGoodsFromDb(String day) {

        List<SeckillGoods> goods = seckillGoodsMapper.getDaySeckillGoods(day);

        return goods;
    }

    @Override
    public SeckillGoods getSeckillGood(Long skuId) {

        SeckillGoods goods = seckillGoodsCacheService.getSeckillGood(skuId);

        return goods;
    }

    @Override
    public long deduceSeckillStockCount(Long skuId, int num) {
        long l = 0;
        try{
            l = seckillGoodsMapper.updateSeckillStockCount(skuId,num);

        }catch (Exception e){
          log.error("skuId: {}秒杀库存不足",skuId);
        }
        return l;
    }

    @Override
    public OrderInfo getSeckillOrder(String code, Long skuId) {
        Long userId = AuthContextHolder.getUserAuth().getUserId();
        String seckillCode = MD5.encrypt(DateUtil.formatDate(new Date()) + userId + skuId);
        if (!seckillCode.equals(code)) {
            throw new GmallException(ResultCodeEnum.SECKILL_ILLEGAL);
        }

        String json = redisTemplate.opsForValue().get(RedisConst.SECKILL_ORDER_PREFIX+ code);
        OrderInfo orderInfo = JSONs.toObj(json, OrderInfo.class);


        return orderInfo;
    }
}




