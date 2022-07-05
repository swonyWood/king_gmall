package com.atguigu.gmall.feign.search;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParam;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.Map;

/**
 * @author Kingstu
 * @date 2022/7/1 19:58
 */

@FeignClient("service-search")
@RequestMapping("/rpc/inner/search")
public interface SearchFeignClient {

    /**
     * 商品检索
     * @param
     * @return
     */
    @PostMapping("/goods")
    Result<Map<String,Object>> search(@RequestBody SearchParam param);

    /**
     * 将商品保存到es
     * @param goods
     * @return
     */
    @PostMapping("/up")
    Result upGoods(@RequestBody Goods goods);

    /**
     * 将商品从es中删除
     * @param skuId
     * @return
     */
    @GetMapping("/down/{skuId}")
    Result downGoods(@PathVariable("skuId")Long skuId);

    /**
     * 增加商品热度分
     * @param skuId
     * @return
     */
    @GetMapping("/incr/hotscore/{skuId}")
    Result incrHotScore(@PathVariable("skuId")Long skuId,
                        @RequestParam("score") Long score);
}
