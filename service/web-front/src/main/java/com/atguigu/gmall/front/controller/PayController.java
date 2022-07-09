package com.atguigu.gmall.front.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Kingstu
 * @date 2022/7/9 20:05
 */
@Controller
public class PayController {

    /**
     * 去到支付页
     * @param orderId
     * @return
     */
    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId") Long orderId){

        return "payment/pay";
    }
}
