package com.atguigu.gmall.search.service.impl;

import com.atguigu.gmall.search.service.GoodsSearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Kingstu
 * @date 2022/7/4 13:41
 */
@SpringBootTest
class GoodsSearchServiceImplTest {

    @Autowired
    GoodsSearchService searchService;

    @Test
    void incrHotScore() {
        searchService.incrHotScore(42L, 11L);
    }
}