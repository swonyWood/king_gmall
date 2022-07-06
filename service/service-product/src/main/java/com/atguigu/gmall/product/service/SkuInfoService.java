package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.math.BigDecimal;
import java.util.List;

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

    /**
     * 根据关键字查询sku列表
     * @param keyword
     * @return
     */
    List<SkuInfo> findSkuInfoByKeyword(String keyword);

    /**
     * 查sku价格
     * @param skuId
     * @return
     */
    BigDecimal getSkuPrice(Long skuId);

    /**
     * 获取所有skuId
     * @return
     */
    List<Long> getSkuIds();

    /**
     * 修改skuInfo
     * @param skuInfo
     */
    void updateSkuInfo(SkuInfo skuInfo);

    /**
     * 根据skuId,得到商品的完整信息
     * @param skuId
     * @return
     */
    Goods getGoodsInfoBySkuId(Long skuId);

    /**
     * 查指定skuId商品,并封装成cartInfo
     * @param skuId
     * @return
     */
    CartInfo getCartInfoBySkuId(Long skuId);

    /**
     * 实时查价
     * @param skuId
     * @return
     */
    BigDecimal get1010SkuPrice(Long skuId);
}
