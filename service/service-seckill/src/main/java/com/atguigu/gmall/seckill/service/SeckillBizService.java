package com.atguigu.gmall.seckill.service;

import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.to.mq.SeckillQueueMsg;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/13 19:42
 */
public interface SeckillBizService {

    /**
     * 提前上架第二天 参与秒杀的商品
     * @param date
     */
    void uploadSeckillGoods(String date);


    /**
     * 生成秒杀码
     * @param skuId
     * @return
     */
    String generateSeckillCode(Long skuId);

    /**
     * 秒杀下单
     * @param skuId
     * @param code
     */
    void seckillOrderSubmit(Long skuId, String code);

    /**
     * 生成秒杀订单
     * @param msg
     */
    void generateSeckillOrder(SeckillQueueMsg msg);

    /**
     * 检查秒杀单
     * @param skuId
     * @return
     */
    ResultCodeEnum checkOrderStatus(Long skuId);

    /**
     * 保存秒杀单
     * @param orderInfo
     * @return
     */
    Long saveSeckillOrder(OrderInfo orderInfo);
}
