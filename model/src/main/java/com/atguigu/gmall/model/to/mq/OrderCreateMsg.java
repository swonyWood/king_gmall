package com.atguigu.gmall.model.to.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * @author Kingstu
 * @date 2022/7/11 17:44
 */
@ToString
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateMsg {

    private Long orderId;
    private Long userId;
    private BigDecimal totalAmount;
    private String status;

}
