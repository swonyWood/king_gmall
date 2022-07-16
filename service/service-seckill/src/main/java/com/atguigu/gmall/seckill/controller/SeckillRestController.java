package com.atguigu.gmall.seckill.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.feign.order.OrderFeignClient;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.seckill.service.SeckillBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Kingstu
 * @date 2022/7/14 13:01
 */
@RestController
@RequestMapping("/api/activity/seckill")
public class SeckillRestController {


    @Autowired
    SeckillBizService seckillBizService;



    /**
     * 生成秒杀码
     * @param skuId
     * @return
     */
    @GetMapping("/auth/getSeckillSkuIdStr/{skuId}")
    public Result generateSeckillCode(@PathVariable("skuId")Long skuId){

        //生成一个秒杀码

        String code = seckillBizService.generateSeckillCode(skuId);
        return Result.ok(code);

    }

    /**
     * 秒杀下单
     * @param skuId
     * @param code
     * @return
     */
    @PostMapping("/auth/seckillOrder/{skuId}")
    public Result seckillCreateOrder(@PathVariable("skuId")Long skuId,
                                     @RequestParam("skuIdStr")String code){

        seckillBizService.seckillOrderSubmit(skuId,code);

        return Result.ok();

    }


    /**
     * 检查秒杀单
     * @param skuId
     * @return
     */
    @GetMapping("/auth/checkOrder/{skuId}")
    public Result checkSeckillOrder(@PathVariable("skuId")Long skuId){

        ResultCodeEnum status = seckillBizService.checkOrderStatus(skuId);
        return Result.build("", status);
    }


    /**
     * 保存秒杀单
     * @param orderInfo
     * @return
     */
    @PostMapping("/auth/submitOrder")
    public Result submitOrder(@RequestBody OrderInfo orderInfo){


        Long orderId = seckillBizService.saveSeckillOrder(orderInfo);
        return Result.ok(orderId+"");
    }



}
