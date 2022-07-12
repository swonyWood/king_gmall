package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.atguigu.gmall.order.domain.OrderDetail
 */
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {

    List<OrderDetail> getOrderDetailsByOrderIdAndUserId(@Param("id") Long id,
                                                        @Param("userId") Long userId);
}




