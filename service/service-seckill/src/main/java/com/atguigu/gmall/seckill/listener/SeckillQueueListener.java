package com.atguigu.gmall.seckill.listener;

import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.model.to.mq.SeckillQueueMsg;
import com.atguigu.gmall.seckill.service.SeckillBizService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @author Kingstu
 * @date 2022/7/14 15:33
 */
@Slf4j
@Service
public class SeckillQueueListener {

    @Autowired
    SeckillBizService seckillBizService;

    @RabbitListener(queues = MqConst.QUEUEU_SECKILL_QUEUE)
    public void  seckillQueue(Message message, Channel channel) throws IOException {
        MessageProperties properties = message.getMessageProperties();
        try {
            //得到秒杀排队消息
            SeckillQueueMsg msg = JSONs.toObj(new String(message.getBody()), SeckillQueueMsg.class);
            //秒杀下单,redis和数据一起扣库存
            seckillBizService.generateSeckillOrder(msg);


        }catch (Exception e) {
            log.error("秒杀失败:{}",e.getStackTrace());
        }finally {
            channel.basicAck(properties.getDeliveryTag(), false);
        }
    }
}
