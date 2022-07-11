package com.atguigu.gmall.pay.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.pay.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kingstu
 * @date 2022/7/11 20:39
 */
@RestController
@RequestMapping("/api/payment/alipay")
public class PayRestController {

    @Autowired
    PayService payService;

    /**
     *为指定的订单生成一个支付页
     * @param orderId
     * @return
     */
    @GetMapping(value = "/submit/{orderId}",produces = "text/html")
    public String getPayPage(@PathVariable("orderId") Long orderId){

        String html = payService.generatePayPage(orderId);

        return html;
    }



}
