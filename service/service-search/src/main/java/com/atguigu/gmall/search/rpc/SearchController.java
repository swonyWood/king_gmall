package com.atguigu.gmall.search.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParam;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import com.atguigu.gmall.search.service.GoodsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Kingstu
 * @date 2022/7/1 19:50
 */
@RequestMapping("/rpc/inner/search")
@RestController
public class SearchController {


    @Autowired
    GoodsSearchService goodsSearchService;

    /**
     * 商品检索
     * @param Param
     * @return
     */
    @PostMapping("/goods")
    public Result<SearchResponseVo> search(@RequestBody SearchParam Param){

        //检索
        SearchResponseVo vo = goodsSearchService.search(Param);
        return Result.ok(vo);
    }
    /**
     * 将商品保存到es
     * @param goods
     * @return
     */
    @PostMapping("/up")
    public Result upGoods(@RequestBody Goods goods){
        goodsSearchService.upGoods(goods);
        return Result.ok();
    }

    /**
     * 将商品从es中删除
     * @param skuId
     * @return
     */
    @GetMapping("/down/{skuId}")
    public Result downGoods(@PathVariable("skuId")Long skuId){
        goodsSearchService.downGoods(skuId);
        return Result.ok();
    }
}
