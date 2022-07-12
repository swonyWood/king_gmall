package com.atguigu.gmall.pay.notify;

import com.alipay.api.AlipayApiException;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.pay.service.PayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 接收支付宝异步通知
 * @author Kingstu
 * @date 2022/7/12 12:44
 */
@Slf4j
@RequestMapping("/rpcapi/payment")
@RestController
public class AlipayNotifyController {


    @Autowired
    PayService payService;

    @Autowired
    RabbitTemplate rabbitTemplate;


    /**
     * http://1aa6-223-76-224-68.jp.ngrok.io/rpcapi/payment/notify/success
     * 支付宝只要发现某个单支付成功,就会给商家指定地址发消息
     * @return
     */
    @PostMapping("/notify/success")
//    @RequestMapping("/notify/success")
    public String paySuccessNotify(@RequestParam Map<String,String> params) throws AlipayApiException {
        //收到消息,修改支付宝状态
        log.info("支付宝异步通知抵达: {}", JSONs.toStr(params));
        //1.验证签名
        boolean checked = payService.checkSign(params);
        if (checked) {
            //验签通过,修改订单状态
            //发消息
            rabbitTemplate.convertAndSend(MqConst.EXCHANGE_ORDER_EVENT,
                    MqConst.RK_ORDER_PAYED,
                    JSONs.toStr(params));

            return "success";
        }
        return "error";
    }
}
