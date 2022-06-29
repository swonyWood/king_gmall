package com.atguigu.gmall.starter.cache;

import com.atguigu.gmall.starter.cache.aspect.CacheAspect;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.atguigu.gmall.starter.cache.component.impl.CacheServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * 商城缓存的自动配置类
 * @author Kingstu
 * @date 2022/6/28 20:59
 */
@AutoConfigureAfter(RedisAutoConfiguration.class)
@Configuration
@Import(RedissonConfiguration.class)
public class MallCacheAutoConfiguration {

    @Bean//缓存切面
    public CacheAspect cacheAspect(){
        return new CacheAspect();
    }

    @Bean//缓存组件
    public CacheService cacheService(){
        return new CacheServiceImpl();
    }
}

