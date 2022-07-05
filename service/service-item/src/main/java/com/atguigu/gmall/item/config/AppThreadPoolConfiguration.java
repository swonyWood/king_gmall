package com.atguigu.gmall.item.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池的配置类
 * @author Kingstu
 * @date 2022/7/4 18:21
 */

@Configuration
public class AppThreadPoolConfiguration {



    @Bean
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolProperties properties){
        ThreadFactory threadFactory = new ThreadFactory() {
            int i = 0;
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "service-item-thread-" + i++);
            }
        };
        return new ThreadPoolExecutor(properties.getCorePoolSize(),
                properties.getMaximumPoolSize(),
                properties.getKeepAliveTime(), TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(properties.getWorkQueueSize()),
                threadFactory,
                new ThreadPoolExecutor.CallerRunsPolicy()
                );
    }




}
