package com.atguigu.gmall.seckill;

import com.atguigu.gmall.common.annotation.EnableFeignInterceptor;
import com.atguigu.gmall.common.annotation.EnableHandleException;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Kingstu
 * @date 2022/7/13 13:27
 */
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.order")
@EnableFeignInterceptor
@EnableRabbit
@EnableHandleException
@MapperScan(basePackages = "com.atguigu.gmall.seckill.mapper")
@SpringCloudApplication
public class SeckillMainApplication {

    public static void main(String[] args) {
        SpringApplication.run(SeckillMainApplication.class,args);
    }
}
