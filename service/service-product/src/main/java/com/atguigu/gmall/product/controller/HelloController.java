package com.atguigu.gmall.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Kingstu
 * @date 2022/6/26 13:17
 */
@RestController
public class HelloController {

    //HashMap<String,byte[]> map = new HashMap<>();
    @GetMapping("/hello")
    public String hello() {
        //map.put(UUID.randomUUID().toString(), new byte[1024*1024]);
        return "ok";
    }

    @Value("${server.port}")
    String port;
    @Autowired
    StringRedisTemplate redisTemplate;
    @GetMapping("/incr")
    public String incr(){
        int anInt = 0;
        System.out.println(":"+port);
        //加锁
        //先尝试去占有锁
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", "1");
        while (!lock) {
            //没有占有就一直去占有
            lock = redisTemplate.opsForValue().setIfAbsent("lock", "1");
        }
        //抢成功,就执行剩下逻辑
        String num = redisTemplate.opsForValue().get("num");
        anInt = Integer.parseInt(num);
        anInt += 1;
        redisTemplate.opsForValue().set("num", anInt+"");
        //释放锁
        redisTemplate.delete("lock");

        return ""+anInt;
    }
}
