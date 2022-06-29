package com.atguigu.gmall.starter.cache.annotation;

import com.atguigu.gmall.starter.cache.MallCacheAutoConfiguration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Kingstu
 * @date 2022/6/28 20:56
 */
@Target({ ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@EnableAspectJAutoProxy //开启基于注解的aspect切面功能
@Import(MallCacheAutoConfiguration.class)
public @interface EnableCache {
}
