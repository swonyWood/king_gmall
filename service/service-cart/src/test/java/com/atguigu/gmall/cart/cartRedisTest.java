package com.atguigu.gmall.cart;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author Kingstu
 * @date 2022/7/5 13:08
 */
@SpringBootTest
public class cartRedisTest {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void test(){
        redisTemplate.opsForValue().set("aaa","aaa1");

        String str = redisTemplate.opsForValue().get("aaa");
        System.out.println("str = " + str);
    }

}
