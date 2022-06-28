package com.atguigu.gmall.common.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kingstu
 * @date 2022/6/27 18:39
 */
@AutoConfigureAfter(RedisAutoConfiguration.class)//redisson必须在redis配置好之后在配置
@Configuration
public class RedissonConfiguration {

    @Autowired
    RedisProperties redisProperties;

    @Bean
    public RedissonClient redissonClient(@Value("${spring.redis.host}")String redisHost){
        //1.redisson的配置
        Config config = new Config();
        String host = redisProperties.getHost();
        int port = redisProperties.getPort();
        String password = redisProperties.getPassword();

        config.useSingleServer()//使用单节点服务器模式
                .setAddress("redis://"+host+":"+port)
                .setPassword(password);

       //2.创建客户端
        RedissonClient redisson = Redisson.create(config);

        return redisson;
    }

}
