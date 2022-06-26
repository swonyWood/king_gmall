package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.model.vo.ValueSkuVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.atguigu.gmall.product.domain.SkuSaleAttrValue
 */
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    /**
     * 查出当前sku对应的spu有多少sku,并每个sku的销售属性值组合,按照值组合位key,skuID为value,存到一个map中 ,再转为json
     * @param spuId
     * @return
     */
    List<ValueSkuVo> getSpu2AllSkuSaleAttrAndValue(@Param("spuId") Long spuId);
}




