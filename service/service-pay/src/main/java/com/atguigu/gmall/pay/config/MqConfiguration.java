package com.atguigu.gmall.pay.config;

import com.atguigu.gmall.common.constant.MqConst;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Kingstu
 * @date 2022/7/11 13:19
 */
@Configuration
public class MqConfiguration {


    /**
     * 订单事件交换机
     * @return
     */
    @Bean
    public Exchange orderEventExchange(){

        return new TopicExchange(MqConst.EXCHANGE_ORDER_EVENT,
                true,
                false);
    }

    /**
     * 订单的延迟队列
     * @return
     */
    @Bean
    public Queue orderDelayQueue() {

        Map<String, Object> arguments = new HashMap<>();

        arguments.put("x-message-ttl", MqConst.ORDER_TIMEOUT);
        arguments.put("x-dead-letter-exchange", MqConst.EXCHANGE_ORDER_EVENT);
        arguments.put("x-dead-letter-routing-key", MqConst.RK_ORDER_TIMEOUT);
        return new Queue(
                MqConst.QUEUE_ORDER_DELAY,
                true,
                false,
                false,
                arguments
        );
    }

    /**
     * 将订单交换机和延迟队列进行绑定
     * @return
     */
    @Bean
    public Binding orderDelayQueueBinding() {

        return new Binding(
                MqConst.QUEUE_ORDER_DELAY,
                Binding.DestinationType.QUEUE,
                MqConst.EXCHANGE_ORDER_EVENT,
                MqConst.RK_ORDER_CREATE,
                null
        );
    }

    /**
     * 订单私信队列
     * 关单服务消费这个队列,可以拿到所有带关闭的超时订单
     * @return
     */
    @Bean
    public Queue orderDeadQueue(){

        return new Queue(
                MqConst.QUEUE_ORDER_DEAD,
                true,
                false,
                false
        );
    }

    /**
     * 将死信队列和交换机利用超时路由键绑定
     * @return
     */
    @Bean
    public Binding orderDeadQueueBinding() {

        return new Binding(
                MqConst.QUEUE_ORDER_DEAD,
                Binding.DestinationType.QUEUE,
                MqConst.EXCHANGE_ORDER_EVENT,
                MqConst.RK_ORDER_TIMEOUT,
                null
        );
    }

}
