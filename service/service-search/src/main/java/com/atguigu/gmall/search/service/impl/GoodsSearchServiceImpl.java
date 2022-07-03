package com.atguigu.gmall.search.service.impl;
import com.atguigu.gmall.model.vo.search.*;
import com.google.common.collect.Lists;

import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.search.repo.GoodsRepository;
import com.atguigu.gmall.search.service.GoodsSearchService;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Kingstu
 * @date 2022/7/1 19:54
 */
@Service
public class GoodsSearchServiceImpl implements GoodsSearchService {

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    ElasticsearchRestTemplate esTemplate;

    @Override
    public void upGoods(Goods goods) {
        goodsRepository.save(goods);
    }

    @Override
    public void downGoods(Long skuId) {
        goodsRepository.deleteById(skuId);
    }

    //检索
    @Override
    public SearchResponseVo search(SearchParam param) {
        //1.根据检索条件构建query
        Query query = buildQuery(param);
        //2.得到命中的记录
        SearchHits<Goods> goods = esTemplate.search(query, Goods.class, IndexCoordinates.of("goods"));
        //3.把命中记录封装成vo
        SearchResponseVo vo = buildResponse(goods,param);
        return vo;
    }

    /**
     * 把命中记录封装成vo
     *
     * @return
     */
    private SearchResponseVo buildResponse(SearchHits<Goods> result,SearchParam param) {

        SearchResponseVo vo = new SearchResponseVo();

        //1.拿到所有查到的商品
        List<Goods> list = new ArrayList<>();
        for (SearchHit<Goods> hit : result.getSearchHits()) {
            Goods good = hit.getContent();
            list.add(good);
        }
        vo.setGoodsList(list);
        //2.当前页码,总页数
        vo.setPageNo(param.getPageNo());
        long hits = result.getTotalHits();
        //pageSize=10
        vo.setTotalPages(hits%10==0?hits/10:hits/10+1);
        //3.检索条件
        vo.setSearchParam(param);
        //4.品牌列表--进阶检索

        List<TrademarkSearchVo> tmList= new ArrayList<>();
        ParsedLongTerms tmIdAgg = result.getAggregations().get("tmIdAgg");
        for (Terms.Bucket bucket : tmIdAgg.getBuckets()) {
            //4.1拿到品牌id
            long tmId = bucket.getKeyAsNumber().longValue();
            //4.2拿到名字
            ParsedStringTerms tmNameAgg = bucket.getAggregations().get("tmNameAgg");
            String tmName = tmNameAgg.getBuckets().get(0).getKeyAsString();
            //4.3拿到logo
            ParsedStringTerms tmLogoAgg = bucket.getAggregations().get("tmLogoAgg");
            String tmLogo = tmLogoAgg.getBuckets().get(0).getKeyAsString();

            //封装vo
            TrademarkSearchVo tmVo = new TrademarkSearchVo();
            tmVo.setTmId(tmId);
            tmVo.setTmName(tmName);
            tmVo.setTmLogoUrl(tmLogo);
            tmList.add(tmVo);
        }
        vo.setTrademarkList(tmList);
        //5.属性列表--进阶检索

        List<AttrSearchVo> attrsList= new ArrayList<>();
        ParsedNested attrAgg = result.getAggregations().get("attrAgg");
        //属性id的值分布
        ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attrIdAgg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            //属性id
            long attrId = bucket.getKeyAsNumber().longValue();
            //属性名
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attrNameAgg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            //属性值
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attrValueAgg");
            ArrayList<String> vals = new ArrayList<>();
            for (Terms.Bucket valueBucket : attrValueAgg.getBuckets()) {
                String value = valueBucket.getKeyAsString();
                vals.add(value);
            }
            //封装vo
            AttrSearchVo searchVo = new AttrSearchVo();
            searchVo.setAttrId(attrId);
            searchVo.setAttrName(attrName);
            searchVo.setAttrValueList(vals);
            attrsList.add(searchVo);
        }
        vo.setAttrsList(attrsList);
        //6.请求路径
        String urlParam= makeUrlParam(param );
        vo.setUrlParam(urlParam);

        //7.品牌面包屑
        String trademark = param.getTrademark();
        if (!StringUtils.isEmpty(trademark)) {
            vo.setTrademarkParam("品牌: "+trademark.split(":")[1]);
        }

        //8.属性面包屑
        if (param.getProps()!=null&&param.getProps().length>0) {
            List<AttrBread> breads = Arrays.stream(param.getProps()).map(str -> {
                String[] split = str.split(":");
                AttrBread attrBread = new AttrBread();
                attrBread.setAttrId(Long.parseLong(split[0]));
                attrBread.setAttrValue(split[1]);
                attrBread.setAttrName(split[2]);
                return attrBread;
            }).collect(Collectors.toList());

            vo.setPropsParamList(breads);
        }
        //9.排序
        //回显orderMap
        String order = param.getOrder();
        OrderMap orderMap = new OrderMap();
        if (!StringUtils.isEmpty(order)) {
            orderMap.setType(order.split(":")[0]);
            orderMap.setSort(order.split(":")[1]);
        }
        vo.setOrderMap(orderMap);

        return vo;
    }

    private String makeUrlParam(SearchParam param) {
        StringBuilder sb = new StringBuilder("/list.html?");

        if (param.getPageNo()!=null) {
            sb.append("&pageNo="+param.getPageNo());
        }

        if (param.getCategory1Id()!=null) {
            sb.append("&category1Id="+param.getCategory1Id());
        }

        if (param.getCategory2Id()!=null) {
            sb.append("&category2Id="+param.getCategory2Id());
        }
        if (param.getCategory3Id()!=null) {
            sb.append("&category3Id="+param.getCategory3Id());
        }
        if (param.getKeyword()!=null) {
            sb.append("&keyword="+param.getKeyword());
        }
        if (!StringUtils.isEmpty(param.getTrademark())) {
            sb.append("&trademark="+param.getTrademark());
        }
        if (param.getProps()!=null&&param.getProps().length>0) {
            Arrays.stream(param.getProps()).forEach(prop->{
                sb.append("&props="+prop);
            });
        }

        return sb.toString();
    }

    /**
     * 根据检索条件构建query
     * dsl结构不确定, 自己编码构造出Query
     *
     * @param param
     * @return
     */
    private Query buildQuery(SearchParam param) {

        //=============查询条件===============
        //2.bool
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //1.总的query
        NativeSearchQuery dsl = new NativeSearchQuery(boolQuery);
        //3.bool-must
        if (param.getCategory3Id() != null) {
            boolQuery.must(QueryBuilders.termQuery("category3Id", param.getCategory3Id()));
        }
        if (param.getCategory2Id() != null) {
            boolQuery.must(QueryBuilders.termQuery("category2Id", param.getCategory2Id()));
        }
        if (param.getCategory1Id() != null) {
            boolQuery.must(QueryBuilders.termQuery("category1Id", param.getCategory1Id()));
        }
        //4.bool-must-match商品
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQuery.must(QueryBuilders.matchQuery("title", param.getKeyword()));
        }
        //5.bool-must-term品牌
        if (!StringUtils.isEmpty(param.getTrademark())) {
            String trademark = param.getTrademark();
            String[] split = trademark.split(":");
            boolQuery.must(QueryBuilders.termQuery("tmId", split[0]));
        }
        //6.bool-must-nested属性-term
        if (param.getProps() != null && param.getProps().length > 0) {
            for (String prop : param.getProps()) {
                String[] split = prop.split(":");
                //6.1构建bool
                BoolQueryBuilder propBool = QueryBuilders.boolQuery();
                propBool.must(QueryBuilders.termQuery("attrs.attrId", split[0]));
                propBool.must(QueryBuilders.termQuery("attrs.attrValue", split[1]));
                //6.1.0构建nested
                boolQuery.must(QueryBuilders.nestedQuery("attrs", propBool, ScoreMode.None));

            }
        }

        //===============排序条件=================
        if (!StringUtils.isEmpty(param.getOrder())) {

            Sort sort = null;
            String[] split = param.getOrder().split(":");
            switch (split[0]) {
                case "1":
                    sort = split[1].equalsIgnoreCase("asc") ? sort.by("hotScore").ascending() : sort.by("hotScore").descending();
                    break;
                case "2":
                    sort = split[1].equalsIgnoreCase("asc") ? sort.by("price").ascending() : sort.by("price").descending();
                    break;
            }

            dsl.addSort(sort);
        }
        //===================分页条件======================
        if (param.getPageNo() != null) {
            //页面从0开始
            PageRequest request = PageRequest.of(param.getPageNo().intValue() - 1, 10);

            dsl.setPageable(request);
        }
        //=======================聚合分析=========================
        //===分析:品牌====
        //1.品牌id-聚合
        TermsAggregationBuilder tmIdAgg = AggregationBuilders.terms("tmIdAgg")
                .field("tmId")
                .size(100);
        //2.品牌id-聚合-品牌名子聚合
        TermsAggregationBuilder tmNameAgg = AggregationBuilders.terms("tmNameAgg")
                .field("tmName")
                .size(1);
        tmIdAgg.subAggregation(tmNameAgg);
        //3.品牌id-聚合-logo子聚合
        TermsAggregationBuilder tmLogoAgg = AggregationBuilders.terms("tmLogoAgg")
                .field("tmLogoUrl")
                .size(1);
        tmIdAgg.subAggregation(tmLogoAgg);
        dsl.addAggregation(tmIdAgg);
        //=========分析:平台属性==========

        NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attrAgg", "attrs");


        TermsAggregationBuilder attrIdAgg = AggregationBuilders.terms("attrIdAgg")
                .field("attrs.attrId")
                .size(100);

        TermsAggregationBuilder attrNameAgg = AggregationBuilders.terms("attrNameAgg")
                .field("attrs.attrName")
                .size(1);
        attrIdAgg.subAggregation(attrNameAgg);
        TermsAggregationBuilder attrValueAgg = AggregationBuilders.terms("attrValueAgg")
                .field("attrs.attrValue")
                .size(100);
        attrIdAgg.subAggregation(attrValueAgg);
        attrAgg.subAggregation(attrIdAgg);
        dsl.addAggregation(attrAgg);


        return dsl;
    }
}
