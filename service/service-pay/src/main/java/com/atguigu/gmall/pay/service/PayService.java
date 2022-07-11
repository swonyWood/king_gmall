package com.atguigu.gmall.pay.service;

import com.alipay.api.AlipayApiException;

/**
 * @author Kingstu
 * @date 2022/7/11 21:16
 */
public interface PayService {
    /**
     * 为指定的订单生成一个支付页
     * @param orderId
     * @return
     */
    String generatePayPage(Long orderId) throws AlipayApiException;
}
