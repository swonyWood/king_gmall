package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.item.ItemFeignClient;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;

/**
 * 商品详情控制器
 * @author Kingstu
 * @date 2022/6/25 9:55
 */
@Controller
public class ItemController {

    @Autowired
    ItemFeignClient itemFeignClient;
    @Autowired
    SkuFeignClient skuFeignClient;

    /**
     * 查询sku详情
     * @param skuId
     * @return
     */
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId")Long skuId, Model model){

        Result<SkuDetailVo> skuDetail = itemFeignClient.getSkuDetail(skuId);
        SkuDetailVo data = skuDetail.getData();

        if(data != null){
            //商品详情服务-查询商品的详情
            //分类
            model.addAttribute("categoryView",data.getCategoryView());
            //sku信息
            model.addAttribute("skuInfo",data.getSkuInfo());
            //sku的价格
            Result<BigDecimal> price = skuFeignClient.getSkuPrice(skuId);
            model.addAttribute("price",price.getData());
            //spu定义的销售属性名和值
            model.addAttribute("spuSaleAttrList",data.getSpuSaleAttrList());
            //得到一个sku对应spu对应所有sku的组合关系
            model.addAttribute("valuesSkuJson",data.getValuesSkuJson());
        }else{
            return "item/error";
        }


        return "item/index";
    }
}
