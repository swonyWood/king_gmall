package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;

import java.util.List;

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

    /**
     * 查出当前购物车
     * @return
     */
    List<CartInfo> getCartAllItem();

    /**
     * 查出指定购物列表
     * @param cartKey
     * @return
     */
    List<CartInfo> getCartAllItem(String cartKey);

    /**
     * 修改商品状态
     * @param skuId
     * @param status
     */
    void updateCartItemStatus(Long skuId, Integer status);

    /**
     * 删除选中的商品
     */
    void deleteChecked();

    /**
     * 拿到所有选中的商品
     * @param cartKey
     * @return
     */
    List<CartInfo> getAllCheckedItem(String cartKey);

    /**
     * 删除购物车指定商品
     * @param skuId
     */
    void deleteCartItem(Long skuId);

    /**
     * 设置临时购物车的过期时间
     */
    void setTempCartExpire();
}
