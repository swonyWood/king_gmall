package com.atguigu.gmall.cart.rpc;

import com.atguigu.gmall.cart.service.CartService;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import com.atguigu.gmall.model.vo.user.UserAuth;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/5 13:22
 */
@RestController
@RequestMapping("/rpc/inner/cart")
public class CartRpcController {


    @Autowired
    CartService cartService;

    /**
     * 获取购物车选中的商品
     * @return
     */
    @GetMapping("/checked/items")
    public Result<List<CartInfo>> getCheckedCartItem(){

        String cartKey = cartService.determinCartKey();

        List<CartInfo> list = cartService.getAllCheckedItem(cartKey);
        return Result.ok(list);
    }

    /**
     * 把skuId商品添加到购物车
     * @return
     */
    @GetMapping("/add/{skuId}")
    public Result<AddSuccessVo> addSkuToCart(@PathVariable("skuId") Long skuId,
                                             @RequestParam("num") Integer num){

        AddSuccessVo vo = cartService.addToCart(skuId, num);
        return Result.ok(vo);
    }


    /**
     * 删除选中的商品
     * @return
     */
    @GetMapping("/delete/checked")
    public Result deleteChecked(){

        cartService.deleteChecked();

        return Result.ok();
    }

}
