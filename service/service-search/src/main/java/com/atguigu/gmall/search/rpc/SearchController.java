package com.atguigu.gmall.search.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParam;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.service.GoodsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.websocket.server.PathParam;

/**
 * @author Kingstu
 * @date 2022/7/1 19:50
 */
@RequestMapping("/rpc/inner/search")
@RestController
public class SearchController {


    @Autowired
    GoodsSearchService searchService;

    /**
     * 商品检索
     * @param Param
     * @return
     */
    @PostMapping("/goods")
    public Result<SearchResponseVo> search(@RequestBody SearchParam Param, HttpServletRequest request){

        //检索
        SearchResponseVo vo = searchService.search(Param);
        return Result.ok(vo);
    }
    /**
     * 将商品保存到es
     * @param goods
     * @return
     */
    @PostMapping("/up")
    public Result upGoods(@RequestBody Goods goods){
        searchService.upGoods(goods);
        return Result.ok();
    }

    /**
     * 将商品从es中删除
     * @param skuId
     * @return
     */
    @GetMapping("/down/{skuId}")
    public Result downGoods(@PathVariable("skuId")Long skuId){
        searchService.downGoods(skuId);
        return Result.ok();
    }

    /**
     * 增加商品热度分
     * @param skuId
     * @return
     */
    @GetMapping("/incr/hotscore/{skuId}")
    public Result incrHotScore(@PathVariable("skuId")Long skuId,
                               @RequestParam("score") Long score){

        searchService.incrHotScore(skuId,score);
        return Result.ok();
    }

}






