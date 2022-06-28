package com.atguigu.gmall.product.service;

/**
 * @author Kingstu
 * @date 2022/6/28 11:19
 */
public interface BloomService {
    /**
     * 初始化布隆
     */
    void initBloom();

    /**
     * 定时重建布隆
     */
    void rebuildSkuBloom();
}
