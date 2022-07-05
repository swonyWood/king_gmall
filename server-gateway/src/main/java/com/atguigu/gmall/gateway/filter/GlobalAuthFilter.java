package com.atguigu.gmall.gateway.filter;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.gateway.properties.AuthProperties;
import com.atguigu.gmall.model.user.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * 拦截索引请求
 * @author Kingstu
 * @date 2022/7/4 20:53
 *
 *
 * 请求的分类
 * 1. 需要登录才能够访问,order
 * 2. 无论如何都不能在浏览器端访问,rpc请求
 * 3. 无论如何都能访问,静态资源
 *
 *
 */
@Slf4j
@Component
public class GlobalAuthFilter implements GlobalFilter {

    @Autowired
    AuthProperties authProperties;

    @Autowired
    StringRedisTemplate redisTemplate;

    //路径匹配器
    AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        String path = request.getURI().getPath();
        //log.info("拦截到请求[{}]",request.getURI().getPath());

        //判断需要拦截还是放弃

        //1./rpc/inner--响应失败
        if (pathMatch(authProperties.getNoAuthUrl(), path)) {
            //rpc/inner --打回去,响应错误的json
            return writeJson(response,ResultCodeEnum.NOAUTH_URL);

        }
        //2.要登录才能访问
        if (pathMatch(authProperties.getLoginUrl(), path)) {
            //3.验证登录的用户: 拿到令牌
            String token = getToken(request);
            UserInfo info = validToken(token);
            if (!StringUtils.isEmpty(info)) {
                //4.token校验通过
                Long id = info.getId();

                //5.放行之前给请求头加UserId字段
                //request不允许
                //request.getHeaders().add("UserId", id.toString());
                ServerHttpRequest newRequest = request.mutate()
                        .header("UserId", id.toString())
                        .build();
                ServerWebExchange newExc = exchange.mutate()
                        .request(newRequest)
                        .response(response)
                        .build();

                //6.放行
                return chain.filter(newExc);
            }else{
                //5.不通过,打回登录页
                log.info("用户令牌[{}]非法,打回登录页",token);
                return locationPage(response,authProperties.getLoginPage());
            }
        }


        //3.既不是非法,也不是要登录请求
        return chain.filter(exchange);
    }

    /**
     * 跳转到指定页面
     * @param response
     * @param loginPage
     * @return
     */
    private Mono<Void> locationPage(ServerHttpResponse response, String loginPage) {

        //设置状态码
        response.setStatusCode(HttpStatus.FOUND);
        //修改响应头
        response.getHeaders().set("Location", loginPage);

        return response.setComplete();
    }

    /**
     * 校验令牌
     * @param token
     * @return
     */
    private UserInfo validToken(String token) {
        //1.没令牌
        if (StringUtils.isEmpty(token)) {
            return null;
        }
        //2.有令牌但是是假的
        UserInfo info = getUserInfo(token);
        if (info == null) {
            return null;
        }

        return info;
    }

    /**
     * 根据令牌去redis检索用户信息
     * @param token
     * @return
     */
    private UserInfo getUserInfo(String token) {
        String json = redisTemplate.opsForValue().get(RedisConst.USER_LOGIN_PREFIX + token);
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        UserInfo userInfo = JSONs.toObj(json, UserInfo.class);
        return userInfo;
    }

    /**
     * 拿到token
     * @param request
     * @return
     */
    private String getToken(ServerHttpRequest request) {
        //1.先看cookie
        String token = request.getCookies().getFirst("token").getValue();
        if (StringUtils.isEmpty(token)){
            //2.没有再去看token
            token = request.getHeaders().getFirst("token");
        }

        return token;
    }

    /**
     * 响应一个json
     * @param response
     * @return
     */
    private Mono<Void> writeJson(ServerHttpResponse response,ResultCodeEnum codeEnum) {
        Result<String> result = Result.build("",codeEnum);
        //浏览器响应json,从response的dataBuffer工厂拿到dataBuffer
        DataBuffer wrap = response.bufferFactory().wrap(JSONs.toStr(result).getBytes(StandardCharsets.UTF_8));
        //指定字符编码
        response.getHeaders().add("content-type", "application/json;charset=utf-8");
        return response.writeWith(Mono.just(wrap));
    }

    /**
     * 路径匹配
     * @param patterns
     * @param path
     * @return >0代表有匹配上的
     */
    private boolean pathMatch(List<String> patterns,String path){

        long count = patterns
                .stream()
                .filter(pattern -> pathMatcher.match(pattern, path))
                .count();
        return count>0;

    }




}




















