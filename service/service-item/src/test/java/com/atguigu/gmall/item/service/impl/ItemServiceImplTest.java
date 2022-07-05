package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Kingstu
 * @date 2022/7/4 18:08
 */
@SpringBootTest
class ItemServiceImplTest {

    @Autowired
    SearchFeignClient searchFeignClient;

    @Test
    void incrHotScore() {
        Result result = searchFeignClient.incrHotScore(49L, 99L);
        System.out.println("result = " + result);
    }
}