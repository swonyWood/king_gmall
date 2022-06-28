package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.BaseSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/6/22 14:58
 * @description
 */

/**
 * 销售属性控制器
 */
@RestController
@RequestMapping("/admin/product")
public class SaleAttrController {

    @Autowired
    BaseSaleAttrService baseSaleAttrService;

    /**
     * 查询所有的销售属性列表
     * @return
     */
    @GetMapping("/baseSaleAttrList")
    public Result  getSaleAttrInfo(){
        List<BaseSaleAttr> list = baseSaleAttrService.list();
        return Result.ok(list);
    }
}
