package com.atguigu.gmall.order.mq;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Kingstu
 * @date 2022/7/11 11:29
 */
@Component
public class heheQueueListener {


    @RabbitListener(queues = "hehe")
    public void testListener(Message message, Channel channel)throws Exception{
        MessageProperties properties = message.getMessageProperties();
        try{
            byte[] bytes = message.getBody();
            System.out.println("收到消息: "+new String(bytes));

            //long deliveryTag, boolean multiple
            channel.basicAck(properties.getDeliveryTag(),false);

        }catch (Exception e){
            //业务失败
            channel.basicNack(properties.getDeliveryTag(),false,true);
        }



    }
}
