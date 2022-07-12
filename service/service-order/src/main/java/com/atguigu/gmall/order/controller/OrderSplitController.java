package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.model.vo.ware.OrderSpiltVo;
import com.atguigu.gmall.model.vo.ware.OrderSplitRespVo;
import com.atguigu.gmall.order.service.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/12 21:11
 */
@RestController
@RequestMapping("/api/order")
public class OrderSplitController {


    @Autowired
    OrderBizService orderBizService;
    /**
     * 库存系统发现商品需要拆单时调用
     * @param vo
     * @return
     */
    @PostMapping("/orderSplit")
    public List<OrderSplitRespVo> splitOrder(OrderSpiltVo vo){
        //订单服务需要按照库存系统返回的商品分布,把大单拆成小单
        List<OrderSplitRespVo> vos = orderBizService.splitOrder(vo);



        return vos;
    }
}
