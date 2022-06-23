package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.SpuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface SpuInfoService extends IService<SpuInfo> {

    /**
     * 商品保存
     * @param spuInfo
     */
    void saveSpuInfo(SpuInfo spuInfo);
}
