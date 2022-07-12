package com.atguigu.gmall.order.service.impl;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;

import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.order.OrderStatusLog;
import com.atguigu.gmall.model.to.mq.WareStockDetail;
import com.atguigu.gmall.model.to.mq.WareStockMsg;
import com.atguigu.gmall.model.vo.order.CartOrderDetailVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderStatusLogService;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
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

    @Autowired
    PaymentInfoService paymentInfoService;

    @Autowired
    RabbitTemplate rabbitTemplate;

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
    public long updateOrderStatus(Long orderId, Long userId, String orderStatus, String processStatus, String expectStatus) {

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
        return l;

    }

    @Transactional
    @Override
    public void orderPayedStatusChange(Map<String, String> map) {
        //1.拿到用户信息
        String tradeNo = map.get("out_trade_no");
        String[] split = tradeNo.split("_");
        long userId = Long.parseLong(split[split.length - 1]);

        //2.拿到订单信息
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", userId);
        wrapper.eq("out_trade_no", map.get("out_trade_no"));
        OrderInfo orderInfo = orderInfoMapper.selectOne(wrapper);

        //3.修改此订单的状态
        ProcessStatus paid = ProcessStatus.PAID;
        long status = orderInfoMapper.updateOrderStatusInExpects(orderInfo.getId(),
                userId,
                paid.getOrderStatus().name(),
                paid.name(),
                Arrays.asList(ProcessStatus.UNPAID.name(), ProcessStatus.CLOSED.name()));

        if (status>0) {//幂等性操作
            //4.推进日志
            OrderStatusLog log = new OrderStatusLog();
            log.setOrderId(orderInfo.getId());
            log.setOrderStatus(paid.getOrderStatus().name());
            log.setOperateTime(new Date());
            log.setUserId(userId);

            orderStatusLogService.save(log);

            //5.支付日志
            paymentInfoService.savePayment(map,orderInfo);
        }

        //6.发消息扣库存
        WareStockMsg msg = pripareWareMsg(orderInfo);
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_WARE_SYSTEM,MqConst.RK_WARE_STOCK, JSONs.toStr(msg));


    }

    @Override
    public OrderInfo getOrderInfoAndDetails(long orderId, long userId) {


        return orderInfoMapper.getOrderInfoAndDetails(orderId,userId);
    }

    private WareStockMsg pripareWareMsg(OrderInfo orderInfo) {
        WareStockMsg msg = new WareStockMsg();
        msg.setOrderId(orderInfo.getId());
        msg.setUserId(orderInfo.getUserId());
        msg.setConsignee(orderInfo.getConsignee());
        msg.setConsigneeTel(orderInfo.getConsigneeTel());
        msg.setOrderComment(orderInfo.getOrderComment());
        msg.setOrderBody(orderInfo.getTradeBody());
        msg.setDeliveryAddress(orderInfo.getDeliveryAddress());
        msg.setPaymentWay("2");

        //订单的详情
        List<OrderDetail> details = orderDetailService.getOrderDetailsByOrderIdAndUserId(orderInfo.getId(), orderInfo.getUserId());

        //List<WareStockDetail>
        List<WareStockDetail> stockDetails = details.stream()
                .map(item -> new WareStockDetail(item.getSkuId(), item.getSkuNum(), item.getSkuName()))
                .collect(Collectors.toList());
        msg.setDetails(stockDetails);

        return msg;
    }


}




