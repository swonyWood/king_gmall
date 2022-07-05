package com.atguigu.gmall.search.service;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.vo.search.SearchParam;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;

/**
 * @author Kingstu
 * @date 2022/7/1 19:54
 */
public interface GoodsSearchService {
    /**
     * es上架商品
     * @param goods
     */
    void upGoods(Goods goods);

    /**
     * 将商品从es中删除
     * @param skuId
     */
    void downGoods(Long skuId);

    /**
     * 商品检索
     * @param param
     * @return
     */
    SearchResponseVo search(SearchParam param);

    /**
     * 增加热度分
     * @param skuId
     * @param score
     */
    void incrHotScore(Long skuId, Long score);
}
