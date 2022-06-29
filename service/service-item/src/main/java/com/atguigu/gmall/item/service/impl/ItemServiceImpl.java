package com.atguigu.gmall.item.service.impl;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.starter.cache.annotation.Cache;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.atguigu.gmall.model.product.SkuInfo;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.model.vo.CategoryView;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author Kingstu
 * @date 2022/6/25 10:54
 */
@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    @Autowired
    SkuFeignClient skuFeignClient;
    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    RedissonClient redissonClient;
    @Autowired
    CacheService cacheService;


    @Cache(key=RedisConst.SKU_INFO_CACHE_KEY_PREFIX+"#{#params[0]}",
            bloomName= RedisConst.SKU_BLOOM_FILTER_NAME,
            bloomIf = "#{#params[0]}",
            tll = RedisConst.SKU_INFO_CACHE_TIMEOUT
    )
    @Override
    public SkuDetailVo getItemDetail(Long skuId) {
        return getItemDetailFromRpc(skuId);
    }

    public SkuDetailVo getItemDetailRedissonLockBloom(Long skuId) {
        //1.先查缓存
        String cacheKey = RedisConst.SKU_INFO_CACHE_KEY_PREFIX+skuId;
        SkuDetailVo data = cacheService.getData(cacheKey,SkuDetailVo.class);
        //2.判断缓存中是否存在
        if (data==null) {
            log.info("sku:{} 详情-缓存没命中,准备回源...正在检索布隆是否存在这个商品",skuId);
            RBloomFilter<Object> filter = redissonClient.getBloomFilter(RedisConst.SKU_BLOOM_FILTER_NAME);
            if (!filter.contains(skuId)) {
                //布隆说没有
                log.info("sku:{} 觉得没有,请求无法穿透",skuId);
                return null;
            }
            //2.1 缓存中没有,准备回源
            //2.1.1加分布式锁
            RLock lock = redissonClient.getLock(RedisConst.SKU_INFO_LOCK_PREFIX + skuId);
            //2.3 加锁
            boolean tryLock = lock.tryLock();
            //2.4 获得锁
            if (tryLock) {
                //2.7准备回源
                SkuDetailVo detail = getItemDetailFromRpc(skuId);
                //2.8 放缓存
                cacheService.saveData(cacheKey,detail,RedisConst.SKU_INFO_CACHE_TIMEOUT,TimeUnit.MILLISECONDS);
                //2.9 解锁
                lock.unlock();

                return detail;

            }else{
                //2.5 没获得锁
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                    //2.6 直接查缓存即可
                    return cacheService.getData(cacheKey,SkuDetailVo.class);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }

        //3.缓存中有这个数据,直接返回
        return data;

    }

//    @Override
//    public SkuDetailVo getItemDetailWithRedisLock(Long skuId) {
//        //1.先查缓存
//        String json = redisTemplate.opsForValue().get("sku:info:" + skuId);
//        //2.判断缓存
//        if (StringUtils.isEmpty(json)) {
//            String token = UUID.randomUUID().toString().replaceAll("-", "");
//            //2.1缓存中没有,准备回源,加分布式锁
//            Boolean absent = redisTemplate.opsForValue().setIfAbsent("lock:" + skuId, "1", 10, TimeUnit.SECONDS);
//            if (absent) {
//                //redisTemplate.expire("lock:" + skuId, 10,TimeUnit.SECONDS);
//                //2.1.1抢到锁
//                SkuDetailVo rpc = getItemDetailFromRpc(skuId);
//                //2.2把数据放入缓存
//                redisTemplate.opsForValue().set("sku:info:" + skuId, JSONs.toStr(rpc), 7, TimeUnit.DAYS);
//                //2.3解锁, 脚本: lua
//                String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
//                //RedisScript<T> script, List<K> keys, Object... args
//                //执行删锁脚本
//                Long execute = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class), Arrays.asList("lock:" + skuId), token);
//                if (execute == 0) {
//                    log.info("又差点删别人的锁了:lockKey={},lockValue={}", "lock:" + skuId, token);
//                }
//                //异步,伴随线程,每多少秒自动续期
//
//
////                String redisToken = redisTemplate.opsForValue().get("lock:" + skuId);
////                if (token.equals(redisToken)) {
////                    //这是我的锁
////                    redisTemplate.delete("lock:" + skuId);
////                }//不是你的锁就不管
//
//                return rpc;
//            }
//            //2.4没抢到锁,等待
//            try {
//                TimeUnit.MINUTES.sleep(300);
//                //2.4直接查缓存
//                json = redisTemplate.opsForValue().get("sku:info:" + skuId);
//                return JSONs.toObj(json, SkuDetailVo.class);
//            } catch (InterruptedException e) {
//
//            }
//
//        }
//        //3.缓存中有,json转对象
//        SkuDetailVo obj = JSONs.toObj(json, SkuDetailVo.class);
//
//        return obj;
//    }

    public SkuDetailVo getItemDetailFromRpc(Long skuId) {
        SkuDetailVo vo = new SkuDetailVo();

        //1.sku的info
        Result<SkuInfo> skuInfo = skuFeignClient.getSkuInfo(skuId);
        SkuInfo info = skuInfo.getData();
        vo.setSkuInfo(info);

        //2.sku所在分类
        Long category3Id = info.getCategory3Id();
        Result<CategoryView> view = skuFeignClient.getCategoryViews(category3Id);
        //按照三级分类id查出所在的完整分类信息
        vo.setCategoryView(view.getData());

        //3.sku的价格
        vo.setPrice(info.getPrice());

        //4.sku的销售属性列表
        Long spuId = info.getSpuId();
        Result<List<SpuSaleAttr>> saleAttr = skuFeignClient.getSaleAttr(skuId, spuId);
        if (saleAttr.isOk()) {
            vo.setSpuSaleAttrList(saleAttr.getData());
        }
        //ValuesSkuJson   {"118:120":49,"119:120":50}
        //5.得到一个sku对应spu对应所有sku的组合关系
        Result<String> value = skuFeignClient.getSpu2AllSkuSaleAttrAndValue(spuId);
        vo.setValuesSkuJson(value.getData());


        return vo;
    }

}
