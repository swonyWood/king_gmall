package com.atguigu.gmall.feign.item;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Kingstu
 * @date 2022/6/25 10:40
 */
@RequestMapping("/rpc/inner/item")
@FeignClient("service-item")
public interface ItemFeignClient {

    /**
     * 获取商品详情
     * @param skuId
     * @return
     */
    @GetMapping("/sku/{skuId}")
    Result<SkuDetailVo> getSkuDetail(@PathVariable("skuId")Long skuId);
}
