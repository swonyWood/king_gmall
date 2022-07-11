package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.service.OrderBizService;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kingstu
 * @date 2022/7/9 13:32
 */
@RequestMapping("/api/order/auth")
@RestController
public class OrderRestController {

    @Autowired
    OrderBizService orderBizService;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderDetailService orderDetailService;

    /**
     * 提交订单
     * @param tradeNo
     * @param order
     * @return
     */
    @PostMapping("/submitOrder")
    public Result submitOrder(@RequestParam("tradeNo") String tradeNo,
                              @RequestBody OrderSubmitVo order){

        //创建订单,并返回订单id//前端无法接收全Long值
        Long id = orderBizService.submitOrder(tradeNo, order);

        return Result.ok(id+"");
    }

    /**
     *
     * @param pageNo
     * @param pageSize
     * @return
     */
    @GetMapping("/{pageNo}/{pageSize}")
    public Result orderList(@PathVariable("pageNo") Integer pageNo,
                            @PathVariable("pageSize") Integer pageSize){


        Page<OrderInfo> page = new Page<>(pageNo,pageSize);
        page.addOrder(OrderItem.desc("id"));

        Long userId = AuthContextHolder.getUserAuth().getUserId();

        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        Page<OrderInfo> result = orderInfoService.page(page,wrapper);

        List<OrderInfo> infos = result.getRecords().stream()
                .map(record -> {

                    QueryWrapper<OrderDetail> detailWrapper = new QueryWrapper<>();
                    detailWrapper.eq("user_id", userId);
                    detailWrapper.eq("order_id", record.getId());

                    List<OrderDetail> list = orderDetailService.list(detailWrapper);
                    record.setOrderDetailList(list);
                    return record;
                }).collect(Collectors.toList());

        result.setRecords(infos);


        return Result.ok(result);

    }



}



















