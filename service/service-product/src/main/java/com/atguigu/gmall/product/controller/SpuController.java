package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/6/22 14:35
 * @description
 */
@RequestMapping("/admin/product")
@RestController
public class SpuController {

    @Autowired
    SpuInfoService spuInfoService;
    @Autowired
    SpuImageService spuImageService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    /**
     * 分页带条件查询
     * @param category3Id
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/{page}/{limit}")
    public Result getSpuByCategoryId(@RequestParam("category3Id")Long category3Id,
                                     @PathVariable("page")Long page,
                                     @PathVariable("limit")Long limit){
        Page<SpuInfo> p = new Page<>(page,limit);
        QueryWrapper<SpuInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("category3_id", category3Id);
        //分页查询
        Page<SpuInfo> result = spuInfoService.page(p, wrapper);

        return Result.ok(result);
    }

    /**
     * 商品保存
     * @return
     */
    @PostMapping("/saveSpuInfo")
    public Result saveSpuInfo(@RequestBody SpuInfo spuInfo){
        spuInfoService.saveSpuInfo(spuInfo);
        return Result.ok();
    }

    /**
     * 查询spuId对应的图片列表
     * @param spuId
     * @return
     */
    @GetMapping("spuImageList/{spuId}")
    public Result getSpuImageList(@PathVariable("spuId")Long spuId){
        QueryWrapper<SpuImage> wrapper = new QueryWrapper<>();
        wrapper.eq("spu_id", spuId);
        List<SpuImage> list = spuImageService.list(wrapper);
        return Result.ok(list);
    }

    /**
     * 查询spuId对应销售属性和值
     * @param spuId
     * @return
     */
    @GetMapping("/spuSaleAttrList/{spuId}")
    public Result getSpuSaleAttrList(@PathVariable("spuId")Long spuId){
        List<SpuSaleAttr> list = spuSaleAttrService.getSpuSaleAttrList(spuId);
        return Result.ok(list);
    }

}














