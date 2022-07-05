package com.atguigu.gmall.model.vo;

import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Kingstu
 * @date 2022/6/25 10:13
 */
@Data
public class SkuDetailVo {

       //sku对应的三级分类
    private CategoryView categoryView;
    //sku信息
    private SkuInfo skuInfo;
    //sku价格
    private BigDecimal price;
    //销售属性列表
    private List<SpuSaleAttr> spuSaleAttrList;
    private String valuesSkuJson;
}
