package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.atguigu.gmall.order.domain.OrderInfo
 */
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {


    Long updateOrderStatus(@Param("orderId") Long orderId,
                           @Param("userId") Long userId,
                           @Param("orderStatus") String orderStatus,
                           @Param("processStatus") String processStatus,
                           @Param("expectStatus") String expectStatus);

    long updateOrderStatusInExpects(@Param("id") Long id,
                                    @Param("userId") long userId,
                                    @Param("name") String name,
                                    @Param("name1") String name1,
                                    @Param("asList") List<String> expectStatus);

    /**
     * 查询指定订单详情
     * @param orderId
     * @param userId
     * @return
     */
    OrderInfo getOrderInfoAndDetails(@Param("orderId") long orderId,
                                     @Param("userId") long userId);
}




