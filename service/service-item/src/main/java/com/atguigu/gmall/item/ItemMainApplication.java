package com.atguigu.gmall.item;

import com.atguigu.gmall.starter.cache.annotation.EnableCache;
import com.atguigu.gmall.starter.cache.annotation.EnableRedisson;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author Kingstu
 * @date 2022/6/25 10:19
 *
 * AOP
 * 1.切面:所有通知方法都放在了一个组件(类), 各个位置由这个类的对象切入执行
 * 2. 通知方法: (filter,拦截器,切面)
 *      目标方法执行之前要拦截到指定位置,来执行通知方法
 *
 *
 *com.atguigu.gmall.common.
 * com.atguigu.gmall.item.
 *
 */
@EnableFeignClients(basePackages = "com.atguigu.gmall.feign.product")
@SpringCloudApplication
public class ItemMainApplication {

    public static void main(String[] args) {
       SpringApplication.run(ItemMainApplication.class,args);
    }
}
