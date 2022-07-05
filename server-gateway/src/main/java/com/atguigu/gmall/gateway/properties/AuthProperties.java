package com.atguigu.gmall.gateway.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/4 21:02
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.auth")
public class AuthProperties {

    private List<String> loginUrl;//需要登录访问
    private List<String> noAuthUrl;//不能访问
    private String loginPage; //登录页地址

}
