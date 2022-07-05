package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;

/**
 * @author Kingstu
 * @date 2022/7/5 13:29
 */
public interface CartService {
    /**
     * 把skuId商品添加到购物车
     * @param skuId
     * @param num
     * @return
     */
    AddSuccessVo addToCart(Long skuId, Integer num);

    /**
     * 返回当前购物车的redis总的key
     * 1.用户登录了,cart:info:userId
     * 2.没登录,cart:info:tempId
     * @return
     */
    String determinCartKey();

    /**
     * 从指定的购物车拿到指定的商品
     * @param cartKey
     * @param skuId
     * @return
     */
    CartInfo getCartItem(String cartKey,Long skuId);

    /**
     * 把商品保存到购物车
     * @param item
     * @param cartKey
     */
    void saveItemToCart(CartInfo item,String cartKey);


    /**
     * 从远程查指定skuId商品,并封装成一个CartInfo
     * @param skuId
     * @return
     */
    CartInfo getCartInfoFromRpc(Long skuId);
}
