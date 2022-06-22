package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseAttrValue;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 *
 */
public interface BaseAttrValueService extends IService<BaseAttrValue> {

    /**
     * 根据id查询属性值
     * @param attrId
     * @return
     */
    List<BaseAttrValue> getAttrValueList(Long attrId);
}
