package com.atguigu.gmall.model.vo.order;

import lombok.Data;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/9 13:37
 */
@Data
public class OrderSubmitVo {

    private String consignee;
    private String consigneeTel;
    private String deliveryAddress;
    private String orderComment;
    private List<CartOrderDetailVo> orderDetailList;



}
