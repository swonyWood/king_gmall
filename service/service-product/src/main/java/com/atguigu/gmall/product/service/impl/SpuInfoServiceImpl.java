package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.SpuImage;
import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.product.SpuSaleAttrValue;
import com.atguigu.gmall.product.service.SpuImageService;
import com.atguigu.gmall.product.service.SpuSaleAttrService;
import com.atguigu.gmall.product.service.SpuSaleAttrValueService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.SpuInfoService;
import com.atguigu.gmall.product.mapper.SpuInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 *
 */
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoMapper, SpuInfo>
    implements SpuInfoService{

    @Autowired
    SpuImageService spuImageService;
    @Autowired
    SpuSaleAttrService spuSaleAttrService;
    @Autowired
    SpuSaleAttrValueService spuSaleAttrValueService;
    @Transactional
    @Override
    public void saveSpuInfo(SpuInfo spuInfo) {
        //保存spuInfo
        save(spuInfo);
        Long id = spuInfo.getId();

        //保存spuImageList到spu_image
        List<SpuImage> images = spuInfo.getSpuImageList();
        for (SpuImage image : images) {
            image.setSpuId(id);
        }
        spuImageService.saveBatch(images);

        //保存spuSaleAttrList到spu_sale_attr
        List<SpuSaleAttr> saleAttrs = spuInfo.getSpuSaleAttrList();
        for (SpuSaleAttr saleAttr : saleAttrs) {
            saleAttr.setSpuId(id);
            spuSaleAttrService.save(saleAttr);
            String saleAttrName = saleAttr.getSaleAttrName();

            //提取spu销售属性的值
            List<SpuSaleAttrValue> attrValues = saleAttr.getSpuSaleAttrValueList();
            for (SpuSaleAttrValue attrValue : attrValues) {
                //填补信息
                attrValue.setSpuId(id);
                attrValue.setSaleAttrName(saleAttrName);
                //保存spuSaleAttrValueList到spu_sale_attr_value
                spuSaleAttrValueService.save(attrValue);
            }
        }
    }
}




