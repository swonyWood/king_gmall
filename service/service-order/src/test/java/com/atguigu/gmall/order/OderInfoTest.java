package com.atguigu.gmall.order;
import java.math.BigDecimal;

import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderStatusLog;
import com.atguigu.gmall.model.payment.PaymentInfo;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderStatusLogService;
import com.atguigu.gmall.order.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;
import java.util.Date;
import com.atguigu.gmall.model.activity.CouponInfo;

import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Kingstu
 * @date 2022/7/8 18:57
 */
@SpringBootTest
public class OderInfoTest {


    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderDetailService orderDetailService;

    @Autowired
    OrderStatusLogService orderStatusLogService;

    @Autowired
    PaymentInfoService paymentInfoService;

    @Test
    void testOthder(){
//        orderDetailSave();
//        paymentInfoSave();
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(65L);
        log.setOrderStatus("445");
        log.setOperateTime(new Date());
        log.setUserId(17L);

        orderStatusLogService.save(log);
    }

    private void paymentInfoSave() {
        PaymentInfo info = new PaymentInfo();
        info.setOutTradeNo("444");
        info.setOrderId(34L);
        info.setPaymentType("343");
        info.setTradeNo("343");
        info.setTotalAmount(new BigDecimal("43340"));
        info.setSubject("343");
        info.setUserId(17L);
        info.setPaymentStatus("434");
        info.setCreateTime(new Date());
        info.setCallbackTime(new Date());
        info.setCallbackContent("34t");


        paymentInfoService.save(info);
    }

    private void orderDetailSave() {
        OrderDetail detail = new OrderDetail();
        detail.setOrderId(3442L);
        detail.setSkuId(344L);
        detail.setSkuName("33");
        detail.setImgUrl("323");
        detail.setOrderPrice(new BigDecimal("330"));
        detail.setSkuNum(33);
        detail.setUserId(17L);
        detail.setHasStock("32");
        detail.setCreateTime(new Date());
        detail.setSplitTotalAmount(new BigDecimal("33"));
        detail.setSplitActivityAmount(new BigDecimal("33"));
        detail.setSplitCouponAmount(new BigDecimal("33"));

        orderDetailService.save(detail);
    }

    @Test
    void testQuery(){

        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", 17L);
        orderInfoService.list(wrapper).stream().forEach(item->{
            System.out.println(item);
        });

    }


    @Test
    void testSharding(){

        OrderInfo info = new OrderInfo();
        info.setConsignee("22");
        info.setConsigneeTel("22");
        info.setTotalAmount(new BigDecimal("22"));
        info.setOrderStatus("1");
        info.setUserId(16L);
        info.setPaymentWay("22");
        info.setDeliveryAddress("22");
        info.setOrderComment("22");
        info.setOutTradeNo("22");
        info.setTradeBody("22");
        info.setCreateTime(new Date());
        info.setExpireTime(new Date());
        info.setProcessStatus("");
        info.setTrackingNo("22");
        info.setParentOrderId(9L);
        info.setImgUrl("33");
        info.setOrderDetailList(Lists.newArrayList());
        info.setWareId("33");
        info.setProvinceId(0L);
        info.setActivityReduceAmount(new BigDecimal("0"));
        info.setCouponAmount(new BigDecimal("0"));
        info.setOriginalTotalAmount(new BigDecimal("0"));
        info.setRefundableTime(new Date());
        info.setFeightFee(new BigDecimal("0"));
        info.setOperateTime(new Date());
        info.setOrderDetailVoList(Lists.newArrayList());
        info.setCouponInfo(new CouponInfo());


        orderInfoService.save(info);
        System.out.println("订单id: "+info.getId());
    }

    @Test
    void testRWSplite(){

    }
}
