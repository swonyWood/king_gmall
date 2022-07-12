package com.atguigu.gmall.common.constant;

/**
 * @author Kingstu
 * @date 2022/7/11 13:23
 */
public class MqConst {

    //mq交换机
    public static final String EXCHANGE_ORDER_EVENT = "order-event-exchange";

    //订单超时
    public static final long ORDER_TIMEOUT = 1000*60*30;


    //消息队列
    public static final String QUEUE_ORDER_DELAY = "order-delay-queue";
    public static final String QUEUE_ORDER_DEAD = "order-dead-queue";
    public static final String QUEUE_ORDER_PAYED = "order-payed-queue";


    //路由key
    public static final String RK_ORDER_TIMEOUT = "order.timeout";
    public static final String RK_ORDER_CREATE = "order.create";
    public static final String RK_ORDER_PAYED = "order.payed";
    public static final String EXCHANGE_WARE_SYSTEM = "exchange.direct.ware.stock";
    public static final String RK_WARE_STOCK = "ware.stock";
    public static final String QUEUE_WARE_ORDER = "queue.ware.order";
}
