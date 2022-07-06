package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/6 12:48
 */
@RestController
@RequestMapping("/api/cart")
public class CartRestController {

    @Autowired
    CartService cartService;

    /**
     * 查出当前购物车列表
     * @return
     */
    @GetMapping("/cartList")
    public Result<List<CartInfo>> cartList(){

        List<CartInfo> infos =  cartService.getCartAllItem();

        //实时改价,异步



        return Result.ok(infos);
    }

    /**
     * 指定购物车商品加一
     * @param skuId
     * @param num
     * @return
     */
    @PostMapping("/addToCart/{skuId}/{num}")
    public Result addToCart(@PathVariable("skuId")Long skuId,
                            @PathVariable("num")Integer num){

        cartService.addToCart(skuId, num);
        return Result.ok();
    }

    /**
     * 修改商品状态
     * @param skuId
     * @param status
     * @return
     */
    @GetMapping("/checkCart/{skuId}/{status}")
    public Result checkCart(@PathVariable("skuId")Long skuId,
                            @PathVariable("status")Integer status){

        cartService.updateCartItemStatus(skuId,status);

        return Result.ok();
    }

    /**
     * 删除购物车指定商品
     * @param skuId
     * @return
     */
    @DeleteMapping("/deleteCart/{skuId}")
    public Result deleteCartItem(@PathVariable("skuId")Long skuId){

        cartService.deleteCartItem(skuId);
        return Result.ok();
    }
}
