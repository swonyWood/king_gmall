package com.atguigu.gmall.feign.seckill;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/13 18:56
 */
@FeignClient("service-seckill")
@RequestMapping("/rpc/inner/seckill")
public interface SeckillFeignClient {

    /**
     * 获取当天参与秒杀的所有商品
     * @return
     */
    @GetMapping("/goods/currentDay")
    Result<List<SeckillGoods>> getCurrentDaySeckillGoods();

    /**
     * 获取某个秒杀商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/goods/detail/{skuId}")
    Result<SeckillGoods> getGoodsDetail(@PathVariable("skuId")Long skuId);
}
