package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import com.atguigu.gmall.model.vo.user.UserAuth;
import io.lettuce.core.RedisClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author Kingstu
 * @date 2022/7/5 13:29
 */
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
        ops.put(cartKey, skuId.toString(), JSONs.toStr(item));

    }

    @Override
    public CartInfo getCartInfoFromRpc(Long skuId) {

        Result<CartInfo> result = skuFeignClient.getCartInfoBySkuId(skuId);

        return result.getData();
    }
}
