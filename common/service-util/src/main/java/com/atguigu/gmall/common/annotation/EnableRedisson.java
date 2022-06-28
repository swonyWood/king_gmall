package com.atguigu.gmall.common.annotation;

import com.atguigu.gmall.common.config.RedissonConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author Kingstu
 * @date 2022/6/28 12:23
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Import(RedissonConfiguration.class)
public @interface EnableRedisson {
}
