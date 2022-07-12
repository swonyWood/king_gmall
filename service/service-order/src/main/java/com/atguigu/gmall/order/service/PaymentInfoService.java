package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 *
 */
public interface PaymentInfoService extends IService<PaymentInfo> {

    /**
     * 保存支付日志
     * @param map
     */
    void savePayment(Map<String, String> map, OrderInfo orderInfo);
}
