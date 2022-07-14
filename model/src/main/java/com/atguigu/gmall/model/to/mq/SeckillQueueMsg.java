package com.atguigu.gmall.model.to.mq;

import com.sun.istack.internal.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Kingstu
 * @date 2022/7/14 15:30
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
public class SeckillQueueMsg {

    private Long userId;
    private Long skuId;
    private String seckillCode;
}
