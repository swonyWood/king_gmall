package com.atguigu.gmall.pay.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kingstu
 * @date 2022/7/12 12:34
 */
@Controller
@RequestMapping("/api/payment")
public class AlipaySuccessController {

    /**
     * 从这里重定向到支付成功页
     * @return
     */
    @GetMapping("/alipay/success")
    public String alipaySuccess(){

        return "redirect:http://payment.gmall.com/payment/success.html";
    }


}
