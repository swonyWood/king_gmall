package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.product.BaseCategory2;
import com.atguigu.gmall.model.product.BaseCategory3;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.atguigu.gmall.product.service.BaseCategory3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/6/20 19:59
 * @description
 */
@RestController
@RequestMapping("/admin/product")
public class CategoryController {

    @Autowired
    BaseCategory1Service baseCategory1Service;

    @Autowired
    BaseCategory3Service baseCategory3Service;

    /**
     * 获取所有一级分类
     * @return
     */
    @GetMapping("/getCategory1")
    public Result getCategory1() {
        List<BaseCategory1> category1s = baseCategory1Service.list();
//        if (category1s.size() > 0) {
//            throw new GmallException(ResultCodeEnum.COUPON_LIMIT_GET);
//        }
        return Result.ok(category1s);
    }

    /**
     * 获取某个一级分类下的二级分类
     * @param category1Id
     * @return
     */
    @GetMapping("/getCategory2/{category1Id}")
    public Result getCategory2(@PathVariable("category1Id") Long category1Id) {
        List<BaseCategory2> baseCategory2s = baseCategory1Service.getCategory2(category1Id);
        return Result.ok(baseCategory2s);
    }

    /**
     * 获取某个二级分类下的所有三级分类
     *
     * @param category2Id
     * @return
     */
    @GetMapping("/getCategory3/{category2Id}")
    public Result getCategory3(@PathVariable("category2Id") Long category2Id) {


        List<BaseCategory3> category3s = baseCategory3Service.getCategory3By2Id(category2Id);
        return Result.ok(category3s);
    }
}
