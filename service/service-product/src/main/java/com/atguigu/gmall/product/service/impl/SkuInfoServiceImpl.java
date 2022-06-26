package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SkuAttrValue;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.atguigu.gmall.product.service.SkuAttrValueService;
import com.atguigu.gmall.product.service.SkuImageService;
import com.atguigu.gmall.product.service.SkuSaleAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SkuInfoService;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@ToString
@Service
@Slf4j
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoMapper, SkuInfo>
    implements SkuInfoService{

    @Autowired
    SkuImageService skuImageService;
    @Autowired
    SkuAttrValueService skuAttrValueService;
    @Autowired
    SkuSaleAttrValueService skuSaleAttrValueService;
    @Autowired
    SkuInfoMapper skuInfoMapper;
    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        log.info("sku信息正在保存:{}",skuInfo);
        //1.包括skuInfo基本信息
        save(skuInfo);
        Long id = skuInfo.getId();
        //2.skuImageList保存到sku_image
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(id);
        }
        skuImageService.saveBatch(skuImageList);
        //3.skuAttrValueList保存到sku_attr_value
        List<SkuAttrValue> attrValues = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue attrValue : attrValues) {
            attrValue.setSkuId(id);
        }
        skuAttrValueService.saveBatch(attrValues);
        //4.skuSaleAttrValueList保存到sku_sale_attr_value
        List<SkuSaleAttrValue> saleAttrValues = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue : saleAttrValues) {
            saleAttrValue.setSkuId(id);
            saleAttrValue.setSpuId(skuInfo.getSpuId());
        }
        skuSaleAttrValueService.saveBatch(saleAttrValues);
        log.info("sku信息保存完成,生成的skuId: {}",id);

    }

    @Override
    public void onsale(Long skuId) {
        //TODO 连接es保存数据
        skuInfoMapper.updateSaleStatus(skuId,1);
    }

    @Override
    public void cancelSale(Long skuId) {
        //TODO 连接es删除数据
        skuInfoMapper.updateSaleStatus(skuId,0);
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        QueryWrapper<SkuInfo> wrapper = new QueryWrapper<>();
        wrapper.like("sku_name", keyword);
        List<SkuInfo> list = list(wrapper);
        return list;
    }
}




