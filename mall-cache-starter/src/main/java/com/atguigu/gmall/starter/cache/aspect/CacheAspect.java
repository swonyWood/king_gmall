package com.atguigu.gmall.starter.cache.aspect;

import com.atguigu.gmall.starter.cache.annotation.Cache;
import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.starter.cache.component.CacheService;
import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.model.vo.SkuDetailVo;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.common.TemplateParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author Kingstu
 * @date 2022/6/28 18:38
 *
 * 缓存切面,执行缓存切入的逻辑
 *
 */
@Aspect
@Component
@Slf4j
@Order
public class CacheAspect {

    @Autowired
    CacheService cacheService;
    @Autowired
    RedissonClient redissonClient;

    SpelExpressionParser parser = new SpelExpressionParser();//表达式解析器

    /**
     * 环绕通知
     * @param pjp
     * @return
     * @throws Throwable
     */
    @Around("@annotation(com.atguigu.gmall.starter.cache.annotation.Cache)")
    public Object cacheAspectAround(ProceedingJoinPoint pjp) {
        Object[] args = pjp.getArgs();//参数

        Object retVal = null;//执行目标方法
        try {
            //前置通知
            String cacheKey = CalculateCacheKey(pjp);
            //1.先查缓存
            //String cacheKey = RedisConst.SKU_INFO_CACHE_KEY_PREFIX+args[0];
            Object cacheData = cacheService.getData(cacheKey, new TypeReference<Object>() {
                @Override
                public Type getType() {
                    MethodSignature signature = (MethodSignature) pjp.getSignature();
                    //根据目标方法的返回值类型指定返回的类型
                    return signature.getMethod().getGenericReturnType();
                }
            });
            //2.是否命中
            if (cacheData != null) {
                //3.命中直接返回
                return cacheData;
            }
            //4.缓存不命中,准备回源
            //判断当前方法是否需要使用布隆过滤器
            Cache cache = getCacheAnnotation(pjp,Cache.class);
            if (StringUtils.isEmpty(cache.bloomName())) {
                //不使用布隆
                return getDataWithLock(pjp, args, cacheKey);
            }else{
                //使用布隆
                //4.1先问布隆
                RBloomFilter<Object> filter = redissonClient.getBloomFilter(cache.bloomName());
                //拿到给布隆判定的条件
                Object bloomIfValue = getBloomIfValue(pjp);
                if (filter.contains(bloomIfValue)) {
                    //4.2布隆说有
                    //4.2.1分布式锁
                    return getDataWithLock(pjp, args, cacheKey);
                }else{
                    //4.3布隆说没有
                    return null;
                }

            }
        } catch (Throwable e) {
            //异常通知
            log.info("切面炸了...{}",e);
            throw new RuntimeException(e);
        } finally {
            //后置通知
        }

    }

    /**
     * 获取布隆条件的值
     * @param pjp
     * @return
     */
    private Object getBloomIfValue(ProceedingJoinPoint pjp) {
        Cache cache = getCacheAnnotation(pjp, Cache.class);
        //拿到布隆判定的条件
        String bloomIf = cache.bloomIf();
        Object expression = calculateExpression(bloomIf, pjp);
        return expression;
    }

    /**
     * 用锁的方式获取db数据
     * @param pjp
     * @param args
     * @param cacheKey
     * @return
     * @throws Throwable
     */
    private Object getDataWithLock(ProceedingJoinPoint pjp, Object[] args, String cacheKey) throws Throwable {

        Object retVal;
        String lockKey = RedisConst.LOCK_PREFIX+cacheKey;
        RLock lock = redissonClient.getLock(lockKey);
        //4.2.2加锁
        boolean tryLock = lock.tryLock();
        if (tryLock) {
            //4.2.3加锁成功,回源
            retVal = pjp.proceed(args);//执行
            //4.2.4放缓存
            Cache cache = getCacheAnnotation(pjp, Cache.class);
            cacheService.saveData(cacheKey, retVal,cache.tll(),TimeUnit.MILLISECONDS);
            //4.2.5解锁&返回
            lock.unlock();
            return retVal;
        }
        //4.2.6没得到锁
        TimeUnit.MILLISECONDS.sleep(500);
        //4.2.7直接查缓存即可
        return cacheService.getData(cacheKey, SkuDetailVo.class);
    }

    /**
     * 获取缓存用的key
     * @param pjp
     * @return
     */
    private String CalculateCacheKey(ProceedingJoinPoint pjp) {
        Cache cache = getCacheAnnotation(pjp,Cache.class);
        //4.得到key值  --categorys --未来取到到的是一个可以动态取值的字符串
        String key = cache.key();

        //5.根据字符串得到表达式,获取值
        String spElValue = calculateExpression(key,pjp).toString();

        return spElValue;
    }

    /**
     * 拿到目标方法的注解对象
     * @param pjp
     * @return
     */
    private <T extends Annotation> T getCacheAnnotation(ProceedingJoinPoint pjp,Class<T> t) {
        //1.拿到目标方法上的@Cache
        MethodSignature signature = (MethodSignature) pjp.getSignature();

        //2.拿到目标方法
        Method method = signature.getMethod();

        //3.获取方法上标注的注解
        return method.getDeclaredAnnotation(t);
    }

    /**
     * 计算spEl
     * @param expressionStr
     * @param pjp
     * @return
     */
    private Object calculateExpression(String expressionStr, ProceedingJoinPoint pjp) {
        //1.得到一个表达式
        Expression expression = parser.parseExpression(expressionStr, new TemplateParserContext());

        //2.准备一个上下问
        EvaluationContext context = new StandardEvaluationContext();
        //支持所有语法(动态扩展所有支持的属性)
        context.setVariable("params", pjp.getArgs());//目标方法参数列表
        context.setVariable("currentDate", DateUtil.formatDate(new Date()));
        context.setVariable("redisson", redissonClient);//指向一个组件

        //3.获取表达式的值
        Object expressionValue = expression.getValue(context, Object.class);
        return expressionValue;
    }


    /**
     * 测SpEl表达式
     * @param args
     */
    public  void testSpEl(String[] args) {

        //1.拿到一个表达式解析器
        SpelExpressionParser parser = new SpelExpressionParser();
        //2.解析得到表达式
        Object[] params0 = new  Object[] {"1","2","x"};
        Object[] params1 = new  Object[] {"1","2","x"};
        Object[] params2 = new  Object[] {"1","2","x"};
        Object[] params3 = new  Object[] {"1","2","x"};
        //Expression expression = parser.parseExpression("new String('hello world').toUpperCase()");
        //Expression expression = parser.parseExpression("sku:info:#{1+1}",new TemplateParserContext());
        Expression expression = parser.parseExpression("sku:info:#{#params0[0]}",new TemplateParserContext());
        //3.获取表达式的值

        EvaluationContext context = new StandardEvaluationContext();

        context.setVariable("params0",params0);
        Object value = expression.getValue(context);
        System.out.println("value = " + value);

    }

}
