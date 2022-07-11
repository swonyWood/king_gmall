package com.atguigu.gmall.order.service.impl;
import java.math.BigDecimal;
import java.util.Date;

import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.order.OrderStatusLog;
import com.atguigu.gmall.model.vo.order.CartOrderDetailVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderStatusLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo>
    implements OrderInfoService{

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    OrderInfoMapper orderInfoMapper;

    @Autowired
    OrderStatusLogService orderStatusLogService;

    @Transactional
    @Override
    public void saveDetail(OrderInfo orderInfo, OrderSubmitVo order) {
        //1.拿到前端提交需要购买的商品
        List<CartOrderDetailVo> detailList = order.getOrderDetailList();

        Long userId = AuthContextHolder.getUserAuth().getUserId();

        //2.得到订单详情
        List<OrderDetail> orderDetails = detailList.stream()
                .map(detail -> {
                    OrderDetail result = new OrderDetail();

                    result.setOrderId(orderInfo.getId());
                    result.setSkuId(detail.getSkuId());
                    result.setSkuName(detail.getSkuName());
                    result.setImgUrl(detail.getImgUrl());
                    result.setOrderPrice(detail.getOrderPrice());
                    result.setSkuNum(detail.getSkuNum());
                    result.setUserId(userId);
                    result.setHasStock(detail.getStock());
                    result.setCreateTime(new Date());
                    //当前商品的总价
                    result.setSplitTotalAmount(detail.getOrderPrice().multiply(new BigDecimal(detail.getSkuNum())));

                    result.setSplitActivityAmount(new BigDecimal("0"));
                    result.setSplitCouponAmount(new BigDecimal("0"));



                    return result;
                }).collect(Collectors.toList());
        //3.保存详情
        orderDetailService.saveBatch(orderDetails);

    }

    @Transactional
    @Override
    public void updateOrderStatus(Long orderId, Long userId, String orderStatus, String processStatus, String expectStatus) {

        Long l = orderInfoMapper.updateOrderStatus(orderId, userId, orderStatus, processStatus, expectStatus);

        if (l>0) {
            //修改数量不为0
            //2.新增修改日志
            OrderStatusLog log = new OrderStatusLog();
            log.setOrderId(orderId);
            log.setOrderStatus(orderStatus);
            log.setOperateTime(new Date());
            log.setUserId(userId);

            orderStatusLogService.save(log);
        }

    }


}




