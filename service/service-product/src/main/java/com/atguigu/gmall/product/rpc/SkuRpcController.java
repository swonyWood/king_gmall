package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.dto.CategoryViewDo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.product.biz.Spu2SkuSaleAttrBizService;
import com.atguigu.gmall.product.service.CategoryViewService;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

/**
 * sku相关的暴露远程接口
 * @author Kingstu
 * @date 2022/6/25 11:33
 */
@RequestMapping("/rpc/inner/product")
@RestController
public class SkuRpcController {
    @Autowired
    SkuInfoService skuInfoService;
    @Autowired
    CategoryViewService categoryViewService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;

    @Autowired
    Spu2SkuSaleAttrBizService spu2SkuSaleAttrBizService;

    /**
     * 查询skuInfo
     * @param skuId
     * @return
     */
    @GetMapping("/skuInfo/{skuId}")
    public Result<SkuInfo> getSkuInfo(@PathVariable("skuId")Long skuId){

        SkuInfo info = skuInfoService.getById(skuId);
        return Result.ok(info);
    }

    /**
     * 按照三级分类id查出所在的完整分类信息
     * @param c3Id
     * @return
     */
    @GetMapping("/categoryview/{c3Id}")
    public Result<CategoryView> getCategoryViews(@PathVariable("c3Id")Long c3Id){
        CategoryViewDo viewDo = categoryViewService.getViewByC3Id(c3Id);
        //把do转成页面需要的vo
        CategoryView view = new CategoryView();

        view.setCategory1Id(viewDo.getId());
        view.setCategory1Name(viewDo.getName());
        view.setCategory2Id(viewDo.getC2id());
        view.setCategory2Name(viewDo.getC2name());
        view.setCategory3Id(viewDo.getC3id());
        view.setCategory3Name(viewDo.getC3name());

        return Result.ok(view);
    }


    /**
     * 根据skuId和spuId查询出当前商品spu定义的所有销售属性名和值以及标记出当前sku是哪一对组合
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping("/sku/saleattr/{skuId}/{spuId}")
    public Result<List<SpuSaleAttr>> getSaleAttr(@PathVariable("skuId")Long skuId,
                                                 @PathVariable("spuId")Long spuId){
        List<SpuSaleAttr> list = spuSaleAttrService.getSpuSaleAttrAndMarkSkuSaleValue(skuId,spuId);
        return Result.ok(list);
    }

    /**
     * 查出当前sku对应的spu有多少sku,并每个sku的销售属性值组合,按照值组合位key,skuID为value,存到一个map中 ,再转为json
     */
    @GetMapping("/spu/skus/saleattrvalue/json/{spuId}")
    public Result<String> getSpu2AllSkuSaleAttrAndValue(@PathVariable("spuId")Long spuId){
        String json = spu2SkuSaleAttrBizService.getSpu2AllSkuSaleAttrAndValue(spuId);
        return Result.ok(json);
    }

    /**
     * 查sku价格
     * @param skuId
     * @return
     */
    @GetMapping("/sku/price/{skuId}")
    public Result<BigDecimal> getSkuPrice(@PathVariable("skuId")Long skuId){

        BigDecimal price = skuInfoService.getSkuPrice(skuId);
        return Result.ok(price);
    }
}
