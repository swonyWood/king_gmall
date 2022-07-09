package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.vo.order.OrderConfirmVo;

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
}
