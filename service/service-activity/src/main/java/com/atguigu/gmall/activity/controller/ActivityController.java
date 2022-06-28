package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.service.ActivityFeign;
import com.atguigu.gmall.activity.service.ActivityInfoService;
import com.atguigu.gmall.activity.service.ActivityRuleService;
import com.atguigu.gmall.activity.service.CouponInfoService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.activity.ActivityInfo;
import com.atguigu.gmall.model.activity.ActivityRule;
import com.atguigu.gmall.model.activity.ActivityRuleVo;
import com.atguigu.gmall.model.activity.CouponInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/6/23 11:18
 * @description
 */
@RestController
@RequestMapping("/admin/activity")
public class ActivityController {

    @Autowired
    ActivityInfoService activityInfoService;
    @Autowired
    CouponInfoService couponInfoService;
    @Autowired
    ActivityRuleService activityRuleService;
    @Autowired
    ActivityFeign activityFeign;

    /**
     *分页查询活动列表
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/activityInfo/{page}/{limit}")
    public Result getActivityInfo(@PathVariable("page")Long page,
                                  @PathVariable("limit")Long limit){
        Page<ActivityInfo> p = new Page<>(page,limit);
        Page<ActivityInfo> result = activityInfoService.page(p);
        return Result.ok(result);
    }

    /**
     * 分页查询优惠券列表
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/couponInfo/{page}/{limit}")
    public Result getCouponInfo(@PathVariable("page")Long page,
                                  @PathVariable("limit")Long limit){
        Page<CouponInfo> p = new Page<>(page,limit);
        Page<CouponInfo> result = couponInfoService.page(p);
        return Result.ok(result);
    }

    /**
     * 保存活动信息
     * @param activityInfo
     * @return
     */
    @PostMapping("/activityInfo/save")
    public Result saveActivityInfo(@RequestBody ActivityInfo activityInfo){
        activityInfoService.save(activityInfo);
        return Result.ok();
    }

    /**
     * 根据活动id删除相应活动
     * @param activityId
     * @return
     */
    @DeleteMapping("/activityInfo/remove/{activityId}")
    public Result removeActivityInfoById(@PathVariable("activityId")Long activityId){
        QueryWrapper<ActivityInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id",activityId);
        activityInfoService.remove(wrapper);
        return Result.ok();
    }

    /**
     * 根据活动id查询活动
     * @param id
     * @return
     */
    @GetMapping("activityInfo/get/{id}")
    public Result getActivityInfoById(@PathVariable("id")Long id){
        QueryWrapper<ActivityInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("id",id);
        ActivityInfo activityInfo = activityInfoService.getOne(wrapper);
        return Result.ok(activityInfo);
    }

    /**
     * 修改活动信息
     * @param activityInfo
     * @return
     */
    @PutMapping("/activityInfo/update")
    public Result updateActivityInfo(@RequestBody ActivityInfo activityInfo){
        activityInfoService.updateById(activityInfo);
        return Result.ok();
    }

    /**
     *根据活动id查询规则
     * @return
     */
    @GetMapping("/activityInfo/findActivityRuleList/{activityId}")
    public Result findActivityRuleList(@PathVariable("activityId")Long activityId){

        QueryWrapper<ActivityRule> wrapper = new QueryWrapper<>();
        wrapper.eq("activity_id",activityId);
        List<ActivityRule> list = activityRuleService.list(wrapper);
        return Result.ok(activityId);
    }

//    @PostMapping("saveActivityRule")
//    public Result saveActivityRule(@RequestBody ActivityRuleVo activityRuleVo) {
//        activityInfoService.saveActivityRule(activityRuleVo);
//        return Result.ok();
//    }

    /**
     * 根据关键字查询sku列表
     * @return
     */
    @GetMapping("/activityInfo/findSkuInfoByKeyword/{keyword}")
    public Result findSkuInfoByKeyword(@PathVariable("keyword")String keyword){
        List<SkuInfo> list = activityInfoService.findSkuInfoByKeyword(keyword);
        return Result.ok(list);
    }

    ///couponInfo/findCouponByKeyword/
}


















