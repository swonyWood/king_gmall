package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderBizService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * 监听支付成功的订单
 * @author Kingstu
 * @date 2022/7/12 19:32
 */
@Slf4j
@Service
public class OrderPayedListener {


    @Autowired
    OrderInfoService orderInfoService;

    @RabbitListener(queues = MqConst.QUEUE_ORDER_PAYED)
    public void orderPayed(Message message, Channel channel) throws IOException {
        MessageProperties properties = message.getMessageProperties();
        Map<String, String> map = null;
        try {
            //1.支付宝异步返回的所有数据
            byte[] body = message.getBody();
            map = JSONs.toObj(new String(body), new TypeReference<Map<String, String>>() {
            });

            //2.修改订单为已支付状态
            orderInfoService.orderPayedStatusChange(map);
           //不批量处理回复
            channel.basicAck(properties.getDeliveryTag(), false);
        }catch (Exception e) {
            log.info("消息消费失败,返回队列: {},异常原因:{}",map,e);
            channel.basicNack(properties.getDeliveryTag(), false, true);

        }


    }
}
