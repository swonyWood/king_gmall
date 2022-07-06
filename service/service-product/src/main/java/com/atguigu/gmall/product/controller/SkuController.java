package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Kingstu
 * @date 2022/6/22 18:16
 * @description
 */

/**
 * sku控制器
 */
@RestController
@RequestMapping("/admin/product")
public class SkuController {

    @Autowired
    SkuInfoService skuInfoService;

    @Autowired
    CacheService cacheService;

    /**
     * 保存skuInfo
     * @param skuInfo
     * @return
     */
    @PostMapping("/saveSkuInfo")
    public Result saveSkuInfo(@RequestBody SkuInfo skuInfo) {
        skuInfoService.saveSkuInfo(skuInfo);
        return Result.ok();
    }

    /**
     * 分页查询sku
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("list/{page}/{limit}")
    public Result list(@PathVariable("page")Long page,
                       @PathVariable("limit")Long limit){
        Page<SkuInfo> p = new Page<>(page, limit);
        Page<SkuInfo> result = skuInfoService.page(p);
        return Result.ok(result);
    }

    /**
     * 上架
     * @param skuId
     * @return
     */
    @GetMapping("/onSale/{skuId}")
    public Result onSale(@PathVariable("skuId")Long skuId){
        skuInfoService.onsale(skuId);
        return Result.ok();
    }

    /**
     * 下架
     * @param skuId
     * @return
     */
    @GetMapping("/cancelSale/{skuId}")
    public Result cancelSale(@PathVariable("skuId")Long skuId){
        skuInfoService.cancelSale(skuId);
        return Result.ok();
    }

    /**
     * 根据关键字查询sku列表
     * @return
     */
    @GetMapping("/findSkuInfoByKeyword/{keyword}")
    public List<SkuInfo> findSkuInfoByKeyword(@PathVariable("keyword")String keyword){
        List<SkuInfo> list =  skuInfoService.findSkuInfoByKeyword(keyword);
        return list;
    }


    /**
     * 改价格
     * @param skuId
     * @return
     */
    @GetMapping("/change/price/{skuId}")
    public Result changePrice(@PathVariable("skuId")Long skuId, BigDecimal price){
//        skuInfoService.cancelSale(skuId);

        //延迟双删
        cacheService.delayDoubleDelete(RedisConst.SKU_PRICE_CACHE_PREFIX+skuId);

        return Result.ok();
    }
}
