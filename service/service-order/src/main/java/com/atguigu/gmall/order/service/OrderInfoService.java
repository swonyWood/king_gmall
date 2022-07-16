package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 *
 */
public interface OrderInfoService extends IService<OrderInfo> {

    /**
     * 保存明细
     * @param orderInfo
     * @param order
     */
    void saveDetail(OrderInfo orderInfo, OrderSubmitVo order);

    /**
     * 修改订单状态
     * @param orderId
     * @param userId
     * @param name
     * @param name1
     * @param expectStatus
     */
    long updateOrderStatus(Long orderId, Long userId, String name, String name1, String expectStatus);


    /**
     * 修改订单为已支付
     * @param map
     */
    void orderPayedStatusChange(Map<String, String> map);

    /**
     * 查询当前订单以及详情
     * @param orderId
     * @param userId
     * @return
     */
    OrderInfo getOrderInfoAndDetails(long orderId, long userId);

    /**
     * 保存秒杀订单
     * @param orderInfo
     * @return
     */
    Long saveScekillOrder(OrderInfo orderInfo);
}
