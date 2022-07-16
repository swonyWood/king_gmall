package com.atguigu.gmall.seckill.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.seckill.service.SeckillBizService;
import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/13 18:51
 */
@RestController
@RequestMapping("/rpc/inner/seckill")
public class SeckillRpcController {

    @Autowired
    SeckillGoodsService seckillGoodsService;

    @Autowired
    SeckillBizService seckillBizService;

    /**
     * 获取当天参与秒杀的所有商品
     * @return
     */
    @GetMapping("/goods/currentDay")
    public Result<List<SeckillGoods>> getCurrentDaySeckillGoods(){

        List<SeckillGoods> goods =  seckillGoodsService.getCurrentDaySeckillGoods();

        return Result.ok(goods);
    }

    /**
     * 获取某个秒杀商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/goods/detail/{skuId}")
    public  Result<SeckillGoods> getGoodsDetail(@PathVariable("skuId")Long skuId){

        SeckillGoods goods = seckillGoodsService.getSeckillGood(skuId);
        return Result.ok(goods);

    }

    /**
     * 获取某个秒杀单详情
     */
    @GetMapping("/order/{skuId}/{code}")
    public Result<OrderInfo> getSeckillOrder(@PathVariable("code")String code,
                                             @PathVariable("skuId")Long skuId){

        OrderInfo info = seckillGoodsService.getSeckillOrder(code,skuId);
        return Result.ok(info);
    }
}
