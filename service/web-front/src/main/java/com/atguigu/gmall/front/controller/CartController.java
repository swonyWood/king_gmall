package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.model.vo.cart.AddSuccessVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Kingstu
 * @date 2022/7/5 13:13
 */
@Controller
public class CartController {

    @Autowired
    CartFeignClient cartFeignClient;

//    public static Map<Thread,HttpServletRequest> threadLocal = new HashMap<>();

    /**
     * 添加商品到购物车
     * @param skuId
     * @param skuNum
     * @return
     */
    @GetMapping("addCart.html")
    public String addCart(@RequestParam("skuId") Long skuId,
                          @RequestParam("skuNum") Integer skuNum,
                          Model model) {




        //有放
//        threadLocal.put(Thread.currentThread(), request);

        Result<AddSuccessVo> result = cartFeignClient.addSkuToCart(skuId, skuNum);

        model.addAttribute("skuInfo",result.getData());
        model.addAttribute("skuNum",skuNum);
        //有删
//        threadLocal.remove(Thread.currentThread());
        return "cart/addCart";
    }
}
