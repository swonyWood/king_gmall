package com.atguigu.gmall.common.constant;

/**
 * @author Kingstu
 * @date 2022/7/11 13:23
 */
public class MqConst {
    public static final String EXCHANGE_ORDER_EVENT = "order-event-exchange";
    public static final String QUEUE_ORDER_DELAY = "order-delay-queue";
    public static final long ORDER_TIMEOUT = 1000*60*30;
    public static final String RK_ORDER_TIMEOUT = "order.timeout";
    public static final String RK_ORDER_CREATE = "order.create";

    public static final String QUEUE_ORDER_DEAD = "order-dead-queue";
}
