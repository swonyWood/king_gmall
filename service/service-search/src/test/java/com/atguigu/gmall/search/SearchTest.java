package com.atguigu.gmall.search;

import com.atguigu.gmall.model.vo.search.SearchParam;
import com.atguigu.gmall.search.service.GoodsSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Kingstu
 * @date 2022/7/3 12:31
 */

@SpringBootTest
public class SearchTest {

    @Autowired
    GoodsSearchService searchService;

    @Test
    void testSearch(){
        SearchParam param = new SearchParam();
        param.setCategory3Id(61L);
//        param.setKeyword("手机");
        searchService.search(param);
    }
}
