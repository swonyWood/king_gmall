package com.atguigu.gmall.product.biz.impl;

import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.model.vo.ValueSkuVo;
import com.atguigu.gmall.product.biz.Spu2SkuSaleAttrBizService;
import com.atguigu.gmall.product.mapper.SkuSaleAttrValueMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Kingstu
 * @date 2022/6/25 15:18
 */
@Service
public class Spu2SkuSaleAttrBizServiceImpl implements Spu2SkuSaleAttrBizService {

    @Autowired
    SkuSaleAttrValueMapper  skuSaleAttrValueMapper;

    @Override
    public String getSpu2AllSkuSaleAttrAndValue(Long spuId) {

        List<ValueSkuVo> skuVos = skuSaleAttrValueMapper.getSpu2AllSkuSaleAttrAndValue(spuId);
        Map<String,String> map = new HashMap<>();
        for (ValueSkuVo vo : skuVos) {
            String skuId = vo.getSku_id();
            String skuValues = vo.getSku_values();
            map.put(skuValues, skuId);
        }
        return JSONs.toStr(map);
    }
}
