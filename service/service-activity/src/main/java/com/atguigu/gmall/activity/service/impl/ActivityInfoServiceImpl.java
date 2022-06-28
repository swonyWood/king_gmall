package com.atguigu.gmall.activity.service.impl;

import com.atguigu.gmall.activity.mapper.ActivityInfoMapper;
import com.atguigu.gmall.activity.service.ActivityFeign;
import com.atguigu.gmall.activity.service.ActivityInfoService;
import com.atguigu.gmall.model.activity.ActivityInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 */
@Service
public class ActivityInfoServiceImpl extends ServiceImpl<ActivityInfoMapper, ActivityInfo>
    implements ActivityInfoService {


    @Autowired
    ActivityFeign activityFeign;

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {

        List<SkuInfo> list = activityFeign.findSkuInfoByKeyword(keyword);
        return list;
    }
}




