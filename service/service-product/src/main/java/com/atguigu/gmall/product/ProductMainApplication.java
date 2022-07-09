package com.atguigu.gmall.product;

import com.atguigu.gmall.common.annotation.EnableHandleException;
import com.atguigu.gmall.starter.cache.annotation.EnableCache;
import com.atguigu.gmall.starter.cache.RedissonConfiguration;
import com.atguigu.gmall.common.config.Swagger2Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author Kingstu
 * @date 2022/6/20 19:34
 * @description
 */
@EnableHandleException
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.search")
@EnableScheduling //开启定时调度
@EnableTransactionManagement
@Import({Swagger2Config.class})
@MapperScan(basePackages = "com.atguigu.gmall.product.mapper")
@SpringCloudApplication
public class ProductMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductMainApplication.class,args);
    }
}
