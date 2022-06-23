package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface SkuInfoService extends IService<SkuInfo> {

    /**
     * 保存skuInfo
     * @param skuInfo
     */
    void saveSkuInfo(SkuInfo skuInfo);

    /**
     * 上架
     * @param skuId
     */
    void onsale(Long skuId);

    /**
     * 下架
     * @param skuId
     */
    void cancelSale(Long skuId);
}
