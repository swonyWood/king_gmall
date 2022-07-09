package com.atguigu.gmall.order.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.order.service.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Kingstu
 * @date 2022/7/8 20:47
 */
@RestController
@RequestMapping("/rpc/inner/order")
public class OrderRpcController {


    @Autowired
    OrderBizService orderBizService;

    /**
     * 获取订单确认数据
     * @return
     */
    @GetMapping("/comfirm/data")
    public Result<OrderConfirmVo> getOrderComfirmData(){

        OrderConfirmVo vo = orderBizService.getOrderComfirmData();

        return Result.ok(vo);
    }
}
