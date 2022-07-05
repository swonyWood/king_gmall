package com.atguigu.gmall.item.service;

import com.atguigu.gmall.model.vo.SkuDetailVo;

/**
 * @author Kingstu
 * @date 2022/6/25 10:54
 */
public interface ItemService {
    /**
     * 查询详情
     * @param skuId
     * @return
     */
    SkuDetailVo getItemDetail(Long skuId);

    /**
     * 增加热度分
     * @param skuId
     */
    void incrHotScore(Long skuId);
}
