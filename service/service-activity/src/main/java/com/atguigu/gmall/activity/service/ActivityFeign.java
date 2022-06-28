package com.atguigu.gmall.activity.service;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/6/23 16:24
 * @description
 */
@FeignClient("service-product")
public interface ActivityFeign {

    /**
     * 根据关键字查询sku列表
     * @return
     */
    @GetMapping("/admin/product/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable("keyword")String keyword);
}
