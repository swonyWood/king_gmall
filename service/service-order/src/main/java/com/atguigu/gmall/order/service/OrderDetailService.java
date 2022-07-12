package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderDetail;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface OrderDetailService extends IService<OrderDetail> {

    /**
     * 订单的详情
     * @param id
     * @param userId
     * @return
     */
    List<OrderDetail> getOrderDetailsByOrderIdAndUserId(Long id, Long userId);
}
