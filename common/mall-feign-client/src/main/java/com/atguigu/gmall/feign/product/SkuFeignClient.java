package com.atguigu.gmall.feign.product;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Kingstu
 * @date 2022/6/25 11:38
 */
@FeignClient("service-product")
@RequestMapping("/rpc/inner/product")
public interface SkuFeignClient {

    /**
     * 查指定skuId商品,并封装成cartInfo
     * @param skuId
     * @return
     */
    @GetMapping("/cartinfo/{skuId}")
    Result<CartInfo> getCartInfoBySkuId(@PathVariable("skuId") Long skuId);

    /**
     * 查询skuInfo
     * @param skuId
     * @return
     */
    @GetMapping("/skuInfo/{skuId}")
    Result<SkuInfo> getSkuInfo(@PathVariable("skuId")Long skuId);

    /**
     * 按照三级分类id查出所在的完整分类信息
     * @param c3Id
     * @return
     */
    @GetMapping("/categoryview/{c3Id}")
    public Result<CategoryView> getCategoryViews(@PathVariable("c3Id")Long c3Id);

    /**
     * 根据skuId和spuId查询出当前商品spu定义的所有销售属性名和值以及标记出当前sku是哪一对组合
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/sku/saleattr/{skuId}/{spuId}")
    public Result<List<SpuSaleAttr>> getSaleAttr(@PathVariable("skuId")Long skuId,
                                                 @PathVariable("spuId")Long spuId);

    /**
     * 查出当前sku对应的spu有多少sku,并每个sku的销售属性值组合,按照值组合位key,skuID为value,存到一个map中 ,再转为json
     */
    @GetMapping("/spu/skus/saleattrvalue/json/{spuId}")
    public Result<String> getSpu2AllSkuSaleAttrAndValue(@PathVariable("spuId")Long spuId);

    /**
     * 查sku价格
     * @param skuId
     * @return
     */
    @GetMapping("/sku/price/{skuId}")
    public Result<BigDecimal> getSkuPrice(@PathVariable("skuId")Long skuId);
}
