package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.vo.SkuDetailVo;

/**
 * @author Kingstu
 * @date 2022/6/25 10:54
 */
public interface ItemService {
    SkuDetailVo getItemDetail(Long skuId);
}
