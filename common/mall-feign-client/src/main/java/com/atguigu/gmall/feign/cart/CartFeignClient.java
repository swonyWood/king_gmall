package com.atguigu.gmall.feign.cart;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Kingstu
 * @date 2022/7/5 15:01
 */
@RequestMapping("/rpc/inner/cart")
@FeignClient("service-cart")
public interface CartFeignClient {

    /**
     * 把skuId商品添加到购物车
     * @return
     */
    @GetMapping("/add/{skuId}")
    Result<AddSuccessVo> addSkuToCart(@PathVariable("skuId") Long skuId,
                                      @RequestParam("num") Integer num);




}
