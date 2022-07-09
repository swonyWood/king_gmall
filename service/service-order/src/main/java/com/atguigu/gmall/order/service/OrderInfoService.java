package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.baomidou.mybatisplus.extension.service.IService;

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
}
