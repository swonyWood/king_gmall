package com.atguigu.gmall.product.biz;

/**
 * @author Kingstu
 * @date 2022/6/25 15:17
 */
public interface Spu2SkuSaleAttrBizService {
    /**
     * 查出当前sku对应的spu有多少sku,并每个sku的销售属性值组合,按照值组合位key,skuID为value,存到一个map中 ,再转为json
     * @param spuId
     * @return
     */
    String getSpu2AllSkuSaleAttrAndValue(Long spuId);
}
