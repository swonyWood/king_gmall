package com.atguigu.gmall.order.service.impl;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.CartOrderDetailVo;
import com.google.common.collect.Lists;

import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.order.service.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Kingstu
 * @date 2022/7/8 20:58
 */
@Service
public class OrderBizServiceImpl implements OrderBizService {

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    SkuFeignClient skuFeignClient;

    @Autowired
    WareFeignClient wareFeignClient;

    @Override
    public OrderConfirmVo getOrderComfirmData() {

        OrderConfirmVo confirmVo = new OrderConfirmVo();

        //1.地址列表
        confirmVo.setUserAddressList( userFeignClient.getUserAddress().getData());

        //2.去获取购物车中选中的商品
        List<CartInfo> checkedItems = cartFeignClient.getCheckedCartItem().getData();

        List<CartOrderDetailVo> detailVos = checkedItems.stream()
                .map(info -> {
                    CartOrderDetailVo detailVo = new CartOrderDetailVo();
                    detailVo.setImgUrl(info.getImgUrl());
                    detailVo.setSkuName(info.getSkuName());
                    //实时查价
                    Result<BigDecimal> result = skuFeignClient.get1010SkuPrice(info.getSkuId());
                    detailVo.setOrderPrice(result.getData());
                    detailVo.setSkuNum(info.getSkuNum());
                    //远程库存
                    String hasStock = wareFeignClient.hasStock(info.getSkuId(), info.getSkuNum());
                    detailVo.setStock(hasStock);
                    return detailVo;
                }).collect(Collectors.toList());

        confirmVo.setDetailArrayList(detailVos);

        //3.总数量
        Integer totalNum = checkedItems.stream()
                .map(info -> info.getSkuNum())
                .reduce((o1, o2) -> o1 + o2)
                .get();
        confirmVo.setTotalNum(totalNum);

        //4.总金额

        BigDecimal totalAmount = detailVos.stream()
                .map(cart -> cart.getOrderPrice().multiply(new BigDecimal(cart.getSkuNum())))
                .reduce((o1, o2) -> o1.add(o2))
                .get();

        confirmVo.setTotalAmount(totalAmount);


        //5.防重令牌
        String tradeNo = UUID.randomUUID().toString().replace("-", "");
        confirmVo.setTradeNo(tradeNo);


        return confirmVo;
    }
}
