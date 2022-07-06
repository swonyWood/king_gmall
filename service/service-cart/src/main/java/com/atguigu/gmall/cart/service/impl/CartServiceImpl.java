package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import com.atguigu.gmall.model.vo.user.UserAuth;
import com.sun.deploy.security.AuthKey;
import io.lettuce.core.RedisClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Kingstu
 * @date 2022/7/5 13:29
 */
@Slf4j
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    SkuFeignClient skuFeignClient;

    @Override
    public AddSuccessVo addToCart(Long skuId, Integer num) {
        AddSuccessVo vo = new AddSuccessVo();
        //1.决定使用哪个购物车
        String cartKey = determinCartKey();

        //3. 尝试从购物车拿到商品
        CartInfo item = getCartItem(cartKey, skuId);
        if (item == null) {
            //3.1 没有就新增
            CartInfo info = getCartInfoFromRpc(skuId);

            vo.setSkuDefaultImg(info.getImgUrl());
            vo.setSkuName(info.getSkuName());
            vo.setId(info.getId());

            //3.2 设置新增数量
            info.setSkuNum(num);
            //3.3 同步redis
            saveItemToCart(info, cartKey);
        }else {
            //3.2 有就修改数量
            item.setSkuNum(item.getSkuNum()+num);

            vo.setSkuDefaultImg(item.getImgUrl());
            vo.setSkuName(item.getSkuName());
            vo.setId(item.getId());

            //3.3 同步到redis
            saveItemToCart(item, cartKey);
        }

        //给临时购物车设置过期时间
        setTempCartExpire();

        return vo;
    }

    @Override
    public String determinCartKey() {
        //1.拿到用户信息
        UserAuth auth = AuthContextHolder.getUserAuth();
        String cartKey = "";
        if (auth.getUserId()!=null) {
            //用户登录了
            cartKey = RedisConst.CART_INFO_PREFIX+auth.getUserId();
        }else{
            cartKey = RedisConst.CART_INFO_PREFIX+auth.getTempId();
        }

        return cartKey;
    }

    @Override
    public CartInfo getCartItem(String cartKey, Long skuId) {

        //1. 拿到一个能操作hash的对象
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        //2. 获取指定购物车,key的商品
        String json = ops.get(cartKey, skuId.toString());

        //3.逆转
        if (StringUtils.isEmpty(json)) {
          return null;
        }
        CartInfo cartInfo = JSONs.toObj(json, CartInfo.class);
        return cartInfo;
    }

    @Override
    public void saveItemToCart(CartInfo item, String cartKey) {
        //1. 拿到一个能操作hash的对象
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        //2. 给redis保存一个hash数据
        Long skuId = item.getSkuId();
        //3.判断购物车是否已满
        if (ops.size(cartKey)<RedisConst.CART_SIZE_LIMIT) {
            ops.put(cartKey, skuId.toString(), JSONs.toStr(item));
        }else {
            throw new GmallException(ResultCodeEnum.OUT_OF_CART);
        }


    }

    @Override
    public CartInfo getCartInfoFromRpc(Long skuId) {

        Result<CartInfo> result = skuFeignClient.getCartInfoBySkuId(skuId);

        return result.getData();
    }

    @Override
    public List<CartInfo> getCartAllItem() {
        //1.是否需要合并: 只要tempId对应的购物车有东西,并且有UserId: 合并操作
        UserAuth auth = AuthContextHolder.getUserAuth();
        if (auth.getUserId()!=null&& !StringUtils.isEmpty(auth.getTempId())) {
            //有可能合并购物车
            //2.如果临时购物车有东西,就合并: 只需要判断临时购物车是否存在
            Boolean hasKey = redisTemplate.hasKey(RedisConst.CART_INFO_PREFIX + auth.getTempId());
            if (hasKey) {
                //3.临时购物车有东西
                List<CartInfo> infos = getCartAllItem(RedisConst.CART_INFO_PREFIX + auth.getTempId());
                //4.搬家到用户购物车
                infos.forEach(tempItem->{
                    addToCart(tempItem.getSkuId(), tempItem.getSkuNum());
                });
                //5.删除临时购物车
                redisTemplate.delete(RedisConst.CART_INFO_PREFIX + auth.getTempId());
            }
        }
        //登录,临时购物车二选一
        String cartKey = determinCartKey();
        List<CartInfo> allItem = getCartAllItem(cartKey);

        RequestAttributes oldRequest = RequestContextHolder.getRequestAttributes();
        //每一个查一下价格
        CompletableFuture.runAsync(()->{
            log.info("提价了一个实时改价的异步任务");
            allItem.forEach(item->{
                RequestContextHolder.setRequestAttributes(oldRequest);
                Result<BigDecimal> price = skuFeignClient.getSkuPrice(item.getSkuId());
                //重置map
                RequestContextHolder.resetRequestAttributes();
                if (!item.getSkuPrice().equals(price.getData())) {
                    log.info("正在后台更新[{}]购物车,[{}]商品的价格: 原[{}], 现[{}]",cartKey,item.getSkuId(),
                            item.getSkuPrice(),price.getData());
                    //发现价格不一样
                    item.setSkuPrice(price.getData());
                    //同步到redis
                    saveItemToCart(item, cartKey);
                }
            });
        });

        return allItem;

    }

    @Override
    public List<CartInfo> getCartAllItem(String cartKey) {
        //1.拿到hash操作对象
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        List<CartInfo> infos = ops.values(cartKey)
                .stream()
                .map(jsonStr -> JSONs.toObj(jsonStr, CartInfo.class))
                .sorted((pre,next)->(int)(next.getCreateTime().getTime()-pre.getCreateTime().getTime()))
                .collect(Collectors.toList());

        return infos;
    }

    @Override
    public void updateCartItemStatus(Long skuId, Integer status) {
        String cartKey = determinCartKey();
        CartInfo info = getCartItem(cartKey, skuId);
        info.setIsChecked(status);

        //同步redis
        saveItemToCart(info, cartKey);
    }

    @Override
    public void deleteChecked() {

        String cartKey = determinCartKey();

        //1.拿到选中的商品
        List<CartInfo> infos = getAllCheckedItem(cartKey);

        //拿到ids
        Object[] ids = infos.stream()
                .map(info -> info.getSkuId().toString())
                .toArray();

        //2.删除他们
        redisTemplate.opsForHash().delete(cartKey, ids);

    }

    @Override
    public List<CartInfo> getAllCheckedItem(String cartKey) {
        HashOperations<String, String, String> ops = redisTemplate.opsForHash();

        List<CartInfo> infos = ops.values(cartKey)
                .stream()
                .map(jsonStr -> JSONs.toObj(jsonStr, CartInfo.class))
                .filter(info -> info.getIsChecked() == 1)
                .collect(Collectors.toList());

        return infos;
    }

    @Override
    public void deleteCartItem(Long skuId) {

        String cartKey = determinCartKey();

        //Long --> String
        redisTemplate.opsForHash().delete(cartKey, skuId.toString());

    }

    @Override
    public void setTempCartExpire() {

        UserAuth auth = AuthContextHolder.getUserAuth();
        //用户只操作临时购物车
        if (!StringUtils.isEmpty(auth.getTempId()) && auth.getUserId()==null) {
            //用户带了临时token

            Boolean hasKey = redisTemplate.hasKey(RedisConst.CART_INFO_PREFIX + auth.getTempId());
            if (hasKey) {
                //有临时购物车设置过期时间
                redisTemplate.expire(RedisConst.CART_INFO_PREFIX + auth.getTempId(),365, TimeUnit.DAYS);
            }
        }
    }
}
