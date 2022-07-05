package com.atguigu.gmall.common.annotation;

import com.atguigu.gmall.common.interceptor.RequestHeaderSetFeignInterceptor;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Kingstu
 * @date 2022/7/5 19:11
 */
@Import(RequestHeaderSetFeignInterceptor.class)
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface EnableFeignInterceptor {
}
