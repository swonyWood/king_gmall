package com.atguigu.gmall.seckill.service.impl;
import java.math.BigDecimal;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.google.common.collect.Lists;
import com.atguigu.gmall.model.activity.CouponInfo;

import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.common.util.MD5;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.mq.SeckillQueueMsg;
import com.atguigu.gmall.seckill.mapper.SeckillGoodsMapper;
import com.atguigu.gmall.seckill.service.SeckillBizService;
import com.atguigu.gmall.seckill.service.SeckillGoodsCacheService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.sql.Time;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Kingstu
 * @date 2022/7/13 19:42
 */
@Service
public class SeckillBizServiceImpl implements SeckillBizService {

    @Autowired
    SeckillGoodsService  seckillGoodsService;

    @Autowired
    SeckillGoodsCacheService seckillGoodsCacheService;

    @Autowired
    StringRedisTemplate redisTemplate;


    @Autowired
    RabbitTemplate rabbitTemplate;

    @Override
    public void uploadSeckillGoods(String day) {

        //1.先去数据库查到指定日期参与秒杀的所有商品
        List<SeckillGoods> goods = seckillGoodsService.getDaySeckillGoodsFromDb(day);

        //2.预热到缓存
        seckillGoodsCacheService.saveToCache(day,goods);

    }

    @Override
    public String generateSeckillCode(Long skuId) {
        //从多级缓存中得到当前商品的信息
        SeckillGoods goods = seckillGoodsCacheService.getSeckillGood(skuId);

        //1.是否到了时间
        Date date = new Date();
        if (!date.after(goods.getStartTime())) {
            throw new GmallException(ResultCodeEnum.SECKILL_NO_START);
        }

        if (!date.before(goods.getEndTime())) {
            throw new GmallException(ResultCodeEnum.SECKILL_END);
        }


        //2.大概校验库存,本地缓存为0,那么redis一定没有了
        if (goods.getStockCount()<=0) {
            //本地没库存
            throw new GmallException(ResultCodeEnum.SECKILL_FINISH);
        }

        //3.生成秒杀码||md5--date+userId+skuId
//        String uuid = UUID.randomUUID().toString();
        Long userId = AuthContextHolder.getUserAuth().getUserId();

        String seckillCode = MD5.encrypt(DateUtil.formatDate(new Date()) + userId + skuId);

        //redis存一份--seckill:code:秒杀吗=0
        redisTemplate.opsForValue().setIfAbsent(RedisConst.SECKILL_CODE_PREFIX+seckillCode, "0",1,TimeUnit.DAYS);

        return seckillCode;
    }

    //秒杀扣库存
    //请求合法,有库存
    @Override
    public void seckillOrderSubmit(Long skuId, String code) {
        //1.请求是否合法--时间,秒杀码
        SeckillGoods goods = seckillGoodsCacheService.getSeckillGood(skuId);

        //1.是否到了时间
        Date date = new Date();
        if (!date.after(goods.getStartTime())) {
            throw new GmallException(ResultCodeEnum.SECKILL_NO_START);
        }

        if (!date.before(goods.getEndTime())) {
            throw new GmallException(ResultCodeEnum.SECKILL_END);
        }


        //校验秒杀码
        if (!redisTemplate.hasKey(RedisConst.SECKILL_CODE_PREFIX+code)) {
            throw new GmallException(ResultCodeEnum.SECKILL_ILLEGAL);
        }

        //多次重复下单
        Long increment = redisTemplate.opsForValue().increment(RedisConst.SECKILL_CODE_PREFIX + code);
        if (increment > 1) {
            //请求已经发过了
            throw new GmallException(ResultCodeEnum.SUCCESS);
        }

        //大概判断库存
        if (goods.getStockCount()<=0) {
            //本地没库存
            throw new GmallException(ResultCodeEnum.SECKILL_FINISH);
        }

        goods.setStockCount(goods.getStockCount()-1);
        //下单,发消息
        Long userId = AuthContextHolder.getUserAuth().getUserId();

        SeckillQueueMsg msg = new SeckillQueueMsg(userId,skuId,code);
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_SECKILL_EVENT, MqConst.RK_SECKILL_QUEUE,JSONs.toStr(msg));


    }

    @Transactional
    @Override
    public void generateSeckillOrder(SeckillQueueMsg msg) {
        Long skuId = msg.getSkuId();
        //扣数据库库存
        long l = seckillGoodsService.deduceSeckillStockCount(skuId,1);

        if (l>0) {
            //扣库存成功,创建订单
            //临时订单,放缓存
        //扣redis库存
            String date = DateUtil.formatDate(new Date());
            //拿到redis中的这个数据
            String json = (String) redisTemplate.opsForHash().get(RedisConst.SECKILL_GOODS_CACHE_PREFIX + date,
                    skuId.toString());
            SeckillGoods goods = JSONs.toObj(json, SeckillGoods.class);
            goods.setStockCount(goods.getStockCount()-1);
            //重新保存redis
            redisTemplate.opsForHash().put(RedisConst.SECKILL_GOODS_CACHE_PREFIX + date,skuId.toString(),JSONs.toStr(goods));

            //发消息,创建订单--给redis存seckill:order:秒杀码=订单json
            saveTempSeckillOrder(msg,0);


        }else{
            saveTempSeckillOrder(msg,1);
        }


    }

    @Override
    public ResultCodeEnum checkOrderStatus(Long skuId) {

        String seckillCode = MD5.encrypt(DateUtil.formatDate(new Date()) +
                AuthContextHolder.getUserAuth().getUserId() + skuId);

        String key = RedisConst.SECKILL_ORDER_PREFIX+seckillCode;


        //1.是否有临时单
        String json = redisTemplate.opsForValue().get(key);
        //1.1没法消息
        if (StringUtils.isEmpty(json)) {
            //没单,排队?没发消息?
            String incr = redisTemplate.opsForValue().get(RedisConst.SECKILL_CODE_PREFIX + seckillCode);
            if (Integer.parseInt(incr)>0) {
                //发过请求
                return ResultCodeEnum.SECKILL_RUN;

            }
        }else{
            //有,临时单是否有id
            //是否为error
            if ("error".equals(json)) {
                //已售罄
                return ResultCodeEnum.SECKILL_FAIL;
            }
            OrderInfo info = JSONs.toObj(json, OrderInfo.class);
            if (info.getId()==null) {
                //没下单
                return ResultCodeEnum.SECKILL_SUCCESS;
            }else {
                return ResultCodeEnum.SECKILL_ORDER_SUCCESS;
            }


        }



        return ResultCodeEnum.SECKILL_FAIL;
    }


    private void saveTempSeckillOrder(SeckillQueueMsg msg,int type) {
        String code = msg.getSeckillCode();
        String key = RedisConst.SECKILL_ORDER_PREFIX+code;
        if(type==1){
            //库存扣失败
            redisTemplate.opsForValue().set(key, "error",1, TimeUnit.DAYS);

        }else if (type == 0){

            SeckillGoods good = seckillGoodsService.getSeckillGood(msg.getSkuId());
            //准备临时订单数据,先保存redis
            OrderInfo orderInfo = prepareTempOrder(msg, good);

            redisTemplate.opsForValue().set(key, JSONs.toStr(orderInfo),1,TimeUnit.DAYS);
        }



    }

    private OrderInfo prepareTempOrder(SeckillQueueMsg msg, SeckillGoods good) {
        OrderInfo orderInfo = new OrderInfo();


        orderInfo.setTotalAmount(good.getCostPrice());
        orderInfo.setOrderStatus(ProcessStatus.UNPAID.getOrderStatus().name());
        orderInfo.setUserId(msg.getUserId());
        orderInfo.setPaymentWay("2");

        orderInfo.setOutTradeNo("ATGUIGU"+ UUID.randomUUID().toString().replaceAll("-",""));
        orderInfo.setTradeBody(good.getSkuName());
        orderInfo.setCreateTime(new Date());
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());
        orderInfo.setImgUrl(good.getSkuDefaultImg());

        //有哪些商品
        List<OrderDetail> details = prepareTempOrderDetail(good,msg);
        orderInfo.setOrderDetailList(details);

        orderInfo.setActivityReduceAmount(new BigDecimal("0"));
        orderInfo.setCouponAmount(good.getPrice().subtract(good.getCostPrice()));
        orderInfo.setOriginalTotalAmount(good.getPrice());
        return orderInfo;
    }

    private List<OrderDetail> prepareTempOrderDetail(SeckillGoods good,SeckillQueueMsg msg) {

        OrderDetail detail = new OrderDetail();
        detail.setSkuId(good.getSkuId());
        detail.setSkuName(good.getSkuName());
        detail.setImgUrl(good.getSkuDefaultImg());
        detail.setOrderPrice(good.getCostPrice());
        detail.setSkuNum(1);
        detail.setUserId(msg.getUserId());
        detail.setHasStock("1");
        detail.setCreateTime(new Date());
        detail.setSplitTotalAmount(good.getCostPrice());


        return Arrays.asList(detail);
    }

}
