package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.product.service.BaseTrademarkService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Kingstu
 * @date 2022/6/21 17:00
 * @description
 */
@RequestMapping("/admin/product")
@RestController

public class TrademarkController {


    @Autowired
    BaseTrademarkService baseTrademarkService;

    /**
     * 分页查询品牌
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/baseTrademark/{page}/{limit}")
    public Result baseTrademark(@PathVariable("page") Long page,
                                @PathVariable("limit") Long limit){
        Page<BaseTrademark> p = new Page<>(page,limit);
        //分页查询
        Page<BaseTrademark> result = baseTrademarkService.page(p);
        return Result.ok(result);
    }

    /**
     * 删除
     * @param id
     * @return
     */
    @DeleteMapping("/baseTrademark/remove/{id}")
    public Result removeBaseTrademark(@PathVariable("id") Long id){
        baseTrademarkService.removeById(id);
        return Result.ok();
    }


    /**
     * 根据id查询品牌信息
     * @param id
     * @return
     */
    @GetMapping("/baseTrademark/get/{id}")
    public Result getBaseTrademarkById(@PathVariable("id") Long id){
        BaseTrademark trademark = baseTrademarkService.getById(id);
        return Result.ok(trademark);
    }

    /**
     * 保存品牌信息
     * @param baseTrademark
     * @return
     */
    @PostMapping("/baseTrademark/save")
    public Result saveBaseTrademark(@RequestBody BaseTrademark baseTrademark){

        baseTrademarkService.save(baseTrademark);
        return Result.ok();
    }

    /**
     * 修改
     * @param baseTrademark
     * @return
     */
    @PutMapping("/baseTrademark/update")
    public Result updateBaseTrademark(@RequestBody BaseTrademark baseTrademark){

        baseTrademarkService.updateById(baseTrademark);
        return Result.ok();
    }

}




















