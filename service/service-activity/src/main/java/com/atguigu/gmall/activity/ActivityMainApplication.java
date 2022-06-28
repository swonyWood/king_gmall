package com.atguigu.gmall.activity;

import com.atguigu.gmall.common.config.Swagger2Config;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.cloud.client.SpringCloudApplication;

/**
 * @author Kingstu
 * @date 2022/6/23 11:39
 * @description
 */
@EnableFeignClients
@EnableTransactionManagement
@Import(Swagger2Config.class)
@MapperScan(basePackages = "com.atguigu.gmall.activity.mapper")
@SpringCloudApplication
public class ActivityMainApplication {
    public static void main(String[] args) {
        SpringApplication.run(ActivityMainApplication.class,args);
    }
}
