package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.model.vo.ware.OrderSpiltVo;
import com.atguigu.gmall.model.vo.ware.OrderSplitRespVo;
import com.atguigu.gmall.model.vo.ware.WareFenBuVo;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/8 20:58
 */
public interface OrderBizService {
    /**
     * 获取订单确认数据
     * @return
     */
    OrderConfirmVo getOrderComfirmData();

    /**
     * 生成一个交易令牌
     * @return
     */
    String generateTradeToken();

    /**
     * 校验交易令牌
     * @param token
     * @return
     */
    boolean checkTradeToken(String token);

    /**
     * 提交订单
     * @param tradeNo
     * @param order
     * @return
     */
    Long submitOrder(String tradeNo, OrderSubmitVo order);

    /**
     * 保存订单
     * @param tradeNo
     * @param order
     */
    OrderInfo saveOrder(String tradeNo, OrderSubmitVo order);

    /**
     * 关单,修改订单状态,日志记录
     * @param orderId
     * @param userId
     */
    void closeOrder(Long orderId, Long userId);
    /**
     * 获取指定订单和用户的订单信息
     * @param id
     * @return
     */
    OrderInfo getOrderInfoAndUserId(Long id);

    /**
     * 拆单
     * @param vo
     * @return
     */
    List<OrderSplitRespVo> splitOrder(OrderSpiltVo vo);

    /**
     * 保存子订单
     * @param orderInfo
     * @param buVo
     * @return
     */
    OrderSplitRespVo saveChildOrder(OrderInfo orderInfo, WareFenBuVo buVo);

}
