package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.product.SkuInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Entity com.atguigu.gmall.product.domain.SkuInfo
 */
public interface SkuInfoMapper extends BaseMapper<SkuInfo> {

    /**
     * 更新sku的上架状态
     * @param skuId
     * @param
     */
    void updateSaleStatus(@Param("skuId") Long skuId, @Param("status") int status);

    /**
     * 查sku价格
     * @param skuId
     * @return
     */
    BigDecimal getSkuPrice(@Param("skuId") Long skuId);

    /**
     * 查询所有skuId
     * @return
     */
    List<Long> getSkuIds();

    /**
     * 根据skuId查询货物信息
     * @param skuId
     * @return
     */
    Goods getGoodsInfoBySkuId(@Param("skuId") Long skuId);
}




