package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.list.Goods;
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
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    SearchFeignClient searchFeignClient;

    ScheduledExecutorService threadPool = Executors.newScheduledThreadPool(4);

    @Override
    public void updateSkuInfo(SkuInfo skuInfo){
        //1.????????????

        //2.????????????
        //1)????????? 80%???ok
        redisTemplate.delete(RedisConst.SKU_INFO_CACHE_KEY_PREFIX+skuInfo.getId());
        //2)????????? 99.99%???ok
        //????????????????????????????????????

        threadPool.schedule(()->redisTemplate.delete(RedisConst.SKU_INFO_CACHE_KEY_PREFIX+skuInfo.getId()),
                10, TimeUnit.SECONDS);
        //??????,?????????????????????
    }



    @Transactional
    @Override
    public void saveSkuInfo(SkuInfo skuInfo) {
        log.info("sku??????????????????:{}",skuInfo);
        //1.??????skuInfo????????????
        save(skuInfo);
        Long id = skuInfo.getId();
        //2.skuImageList?????????sku_image
        List<SkuImage> skuImageList = skuInfo.getSkuImageList();
        for (SkuImage skuImage : skuImageList) {
            skuImage.setSkuId(id);
        }
        skuImageService.saveBatch(skuImageList);
        //3.skuAttrValueList?????????sku_attr_value
        List<SkuAttrValue> attrValues = skuInfo.getSkuAttrValueList();
        for (SkuAttrValue attrValue : attrValues) {
            attrValue.setSkuId(id);
        }
        skuAttrValueService.saveBatch(attrValues);
        //4.skuSaleAttrValueList?????????sku_sale_attr_value
        List<SkuSaleAttrValue> saleAttrValues = skuInfo.getSkuSaleAttrValueList();
        for (SkuSaleAttrValue saleAttrValue : saleAttrValues) {
            saleAttrValue.setSkuId(id);
            saleAttrValue.setSpuId(skuInfo.getSpuId());
        }
        skuSaleAttrValueService.saveBatch(saleAttrValues);
        log.info("sku??????????????????,?????????skuId: {}",id);

        //????????????
        RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
        filter.add(skuInfo.getId());

    }

    @Override
    public void onsale(Long skuId) {
        //1.?????????????????????
        skuInfoMapper.updateSaleStatus(skuId,1);
        //2.???????????????es???
        Goods goods = this.getGoodsInfoBySkuId(skuId);
        //3.??????????????????????????????
        searchFeignClient.upGoods(goods);
    }

    @Override
    public void cancelSale(Long skuId) {
        //1.?????????????????????
        skuInfoMapper.updateSaleStatus(skuId,0);
        //2.??????????????????????????????
        searchFeignClient.downGoods(skuId);
    }

    @Override
    public List<SkuInfo> findSkuInfoByKeyword(String keyword) {
        QueryWrapper<SkuInfo> wrapper = new QueryWrapper<>();
        wrapper.like("sku_name", keyword);
        List<SkuInfo> list = list(wrapper);
        return list;
    }

    @Override
    public BigDecimal getSkuPrice(Long skuId) {

        return skuInfoMapper.getSkuPrice(skuId);
    }

    @Override
    public List<Long> getSkuIds() {
        return skuInfoMapper.getSkuIds();
    }

    @Override
    public Goods getGoodsInfoBySkuId(Long skuId) {
        Goods goods = skuInfoMapper.getGoodsInfoBySkuId(skuId);
        return goods;
    }
}




