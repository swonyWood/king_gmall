package com.atguigu.gmall.item;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Kingstu
 * @date 2022/6/25 10:19
 */
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@SpringCloudApplication
public class ItemMainApplication {

    public static void main(String[] args) {
       SpringApplication.run(ItemMainApplication.class,args);
    }
}
