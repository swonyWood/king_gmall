package com.atguigu.gmall.product.biz.impl;



import com.atguigu.gmall.starter.cache.annotation.Cache;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.model.vo.CategoryVo;
import com.atguigu.gmall.product.biz.CategoryBizService;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Kingstu
 * @date 2022/6/24 13:12
 */
@Service
public class CategoryBizServiceImpl implements CategoryBizService {

    @Autowired
    BaseCategory1Mapper category1Mapper;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Cache(key="categorys")
    @Override
    public List<CategoryVo> getCategorys() {
        return category1Mapper.getCategorys();
    }

    //整合缓存
    public List<CategoryVo> getCategorys111() {
        //1.先看缓存
        String categorys = redisTemplate.opsForValue().get("categorys");
        //2.缓存中没有
        if (StringUtils.isEmpty(categorys)) {
            //查库之前先上锁
            Boolean absent = redisTemplate.opsForValue().setIfAbsent("lock", "11");//setNx
            if (absent) {
                //抢到了锁,才去查库
                //3.查数据库
                List<CategoryVo> list = category1Mapper.getCategorys();
            }else{
                //没抢到,等一会儿
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //TODO 把以下逻辑加到上锁代码中
            List<CategoryVo> list = category1Mapper.getCategorys();
            //4. 放到缓存,无论数据库有没有数据都放缓存
            if(list==null){
                //数据库中没有,缓存时间短一点
                redisTemplate.opsForValue().set("categorys", JSONs.toStr(null),30, TimeUnit.MINUTES);
            }else{
                //数据库中有,缓存时间长一点
                redisTemplate.opsForValue().set("categorys", JSONs.toStr(list),7, TimeUnit.DAYS);
            }

            //结束方法
            return list;
        }
        //5.缓存中有,转对象
        List<CategoryVo> data = JSONs.toObj(categorys, new TypeReference<List<CategoryVo>>() {
        });
        return data;
    }


}
