package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author Kingstu
 * @date 2022/7/8 20:36
 */
@Controller
public class OrderController {

    @Autowired
    OrderFeignClient orderFeignClient;


    /**
     * 跳到订单确认页
     * @return
     */
    @GetMapping("/trade.html")
    public String orderConfirmPage(Model model){

        Result<OrderConfirmVo> result = orderFeignClient.getOrderComfirmData();
        OrderConfirmVo data = result.getData();

        //1.所有选中需要结算的商品
        model.addAttribute("detailArrayList",data.getDetailArrayList());
        //2.商品数量
        model.addAttribute("totalNum",data.getTotalNum());
        //3.商品总价
        model.addAttribute("totalAmount",data.getTotalAmount());
        //4.用户地址列表
        model.addAttribute("userAddressList",data.getUserAddressList());
        //5.流水号,追踪号,防重令牌
        model.addAttribute("tradeNo",data.getTradeNo());


        return "order/trade";
    }
}
