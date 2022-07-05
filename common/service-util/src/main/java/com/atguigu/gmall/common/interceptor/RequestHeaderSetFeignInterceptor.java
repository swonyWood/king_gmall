package com.atguigu.gmall.common.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author Kingstu
 * @date 2022/7/5 16:38
 */
@Component
public class RequestHeaderSetFeignInterceptor implements RequestInterceptor {
    @Override
    public void apply(RequestTemplate template) {

        //拿到老请求
//        HttpServletRequest req = CartController.threadLocal.get(Thread.currentThread());
        //1.先从Spring提供的RequestContextHolder中拿到当前线程的请求对象
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        //2.拿到当前请求对象
        HttpServletRequest request = attributes.getRequest();

        //3.把原来的请求头放到模板对象
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            //请求头名
            String headerName = headerNames.nextElement();
            //值
            String headerValue = request.getHeader(headerName);
            if ("UserTempId".equalsIgnoreCase(headerName)||"UserId".equalsIgnoreCase(headerName)) {
                template.header(headerName, headerValue);
            }

        }

    }
}
