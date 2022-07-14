package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.seckill.SeckillFeignClient;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/13 13:47
 */
@Controller
public class SeckillController {

    @Autowired
    SeckillFeignClient seckillFeignClient;


    /**
     * 获取秒杀页带列表
     * @param model
     * @return
     */
    @GetMapping("/seckill.html")
    public  String seckillPage(Model model){

        Result<List<SeckillGoods>> goods = seckillFeignClient.getCurrentDaySeckillGoods();

        //skuId,skuDefaultImg,skuName,costPrice,price,num,stockCount,skuId
        model.addAttribute("list",goods.getData());

        return "seckill/index";
    }

    /**
     * 获取秒杀商品详情
     * @param skuId
     * @param model
     * @return
     */
    @GetMapping("/seckill/{skuId}.html")
    public String seckillGoodsDetail(@PathVariable("skuId")Long skuId,Model model){

        Result<SeckillGoods> result = seckillFeignClient.getGoodsDetail(skuId);
        model.addAttribute("item",result.getData());
        return "seckill/item";
    }

    /**
     * 秒杀排队页
     * @param skuId
     * @param code
     * @return
     *
     * '/seckill/queue.html?skuId='+this.skuId+'&skuIdStr='+skuIdSt
     */
    @GetMapping("/seckill/queue.html")
    public String queue(@RequestParam("skuId") Long skuId,
                        @RequestParam("skuIdStr")String code,
                        Model model){

        model.addAttribute("skuId",skuId);
        model.addAttribute("skuIdStr",code);

        return "seckill/queue";
    }

}
