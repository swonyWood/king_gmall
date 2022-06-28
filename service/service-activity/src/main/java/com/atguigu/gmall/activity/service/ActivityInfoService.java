package com.atguigu.gmall.activity.service;

import com.atguigu.gmall.model.activity.ActivityInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 *
 */
public interface ActivityInfoService extends IService<ActivityInfo> {

    /**
     * 根据关键字查询sku列表
     * @param keyword
     * @return
     */
    @GetMapping
    List<SkuInfo> findSkuInfoByKeyword(String keyword);
}
