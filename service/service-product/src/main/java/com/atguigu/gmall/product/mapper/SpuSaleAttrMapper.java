package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.atguigu.gmall.product.domain.SpuSaleAttr
 */
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     * 查询spuId对应销售属性和值
     * @param spuId
     * @return
     */
    List<SpuSaleAttr> getSpuSaleAttrList(@Param("spuId") Long spuId);
}




