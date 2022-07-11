package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Kingstu
 * @date 2022/7/9 20:05
 */
@Controller
public class PayController {

    @Autowired
    OrderFeignClient orderFeignClient;

    /**
     * 去到支付页
     * @param orderId
     * @return
     */
    @GetMapping("/pay.html")
    public String payPage(@RequestParam("orderId") Long orderId, Model model){

        Result<OrderInfo> result = orderFeignClient.getOrderInfoAndUserId(orderId);
        OrderInfo data = result.getData();
        if (OrderStatus.UNPAID.name().equals(data.getOrderStatus())) {
            //id,totalAmount
            model.addAttribute("orderInfo",data);
            return "payment/pay";
        }

        return "redirect:/myOrder.html";

    }

    /**
     * 去支付成功页
     * @return
     */
    @GetMapping("/payment/success.html")
    public String paySuccessPage(){

        return "payment/success";
    }
}
