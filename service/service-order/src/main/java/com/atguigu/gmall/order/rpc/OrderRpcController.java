package com.atguigu.gmall.order.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.order.service.OrderBizService;
import com.atguigu.gmall.order.service.OrderInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @author Kingstu
 * @date 2022/7/8 20:47
 */
@RestController
@RequestMapping("/rpc/inner/order")
public class OrderRpcController {


    @Autowired
    OrderBizService orderBizService;

    @Autowired
    OrderInfoService orderInfoService;



    /**
     * 获取订单确认数据
     * @return
     */
    @GetMapping("/comfirm/data")
    public Result<OrderConfirmVo> getOrderComfirmData(){

        OrderConfirmVo vo = orderBizService.getOrderComfirmData();

        return Result.ok(vo);
    }

    /**
     * 获取指定订单和用户的订单信息
     * @param id
     * @return
     */
    @GetMapping("/info/{id}")
    public Result<OrderInfo> getOrderInfoAndUserId(@PathVariable("id")Long id){

        OrderInfo info = orderBizService.getOrderInfoAndUserId(id);
        return Result.ok(info);

    }

    /**
     * 保存秒杀订单
     * @return
     */
    @PostMapping("/save/seckill/order")
    public Result<Long> saveScekillOrder(@RequestBody OrderInfo orderInfo){
        orderInfo.setExpireTime(new Date());
        Long id = orderInfoService.saveScekillOrder(orderInfo);

        return Result.ok(id);
    }
}
