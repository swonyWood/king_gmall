package com.atguigu.gmall.product;

import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.product.mapper.SkuImageMapper;
import com.atguigu.gmall.product.mapper.SkuInfoMapper;
import org.apache.shardingsphere.infra.hint.HintManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * @author Kingstu
 * @date 2022/6/30 19:18
 */
@SpringBootTest
public class ReadWriteSpTest {

    @Autowired
    SkuInfoMapper skuInfoMapper;

    @Autowired
    SkuImageMapper skuImageMapper;

    //@Transactional  数据一致性非常高
    @Test
    void testWrite(){
        SkuImage skuImage = new SkuImage();
        skuImage.setSkuId(1L);
        skuImage.setImgName("aa");
        skuImage.setImgUrl("aa");
        skuImage.setSpuImgId(1L);
        skuImage.setIsDefault("aa");
        skuImage.setId(1L);
        skuImageMapper.insert(skuImage);


        //设置仅写路由,强制使用主库
        HintManager.getInstance()
                .setWriteRouteOnly();
        //读取 如果是一个事务,强制把读发给主库
        SkuImage skuImage1 = skuImageMapper.selectById(skuImage.getId());
        System.out.println("skuImage1 = " + skuImage1);

    }
    @Test
    void testReadLb(){
        for (int i = 0; i < 2; i++) {
            BigDecimal skuPrice = skuInfoMapper.getSkuPrice(49L);
            System.out.println("skuPrice = " + skuPrice);
        }
    }
}
