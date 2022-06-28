package com.atguigu.gmall.product;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

/**
 * @author Kingstu
 * @date 2022/6/26 18:24
 */
@SpringBootTest
public class RedisTest {

    @Autowired
    StringRedisTemplate redisTemplate;

    @Test
    void  redisTest01(){
        redisTemplate.opsForValue().set("hello", "world");
        System.out.println("over");
    }

    @Test
    void queryTest02(){
        String str = redisTemplate.opsForValue().get("hello");
        System.out.println(str);
    }
}
