package com.atguigu.gmall.item.controller;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author Kingstu
 * @date 2022/6/27 19:39
 */
@RestController
@RequestMapping("/lock")
public class LockTestController {

    @Autowired
    RedissonClient redissonClient;

    @GetMapping("/a")
    public String a(){
        String lockKey = "lock-test";
        //1.获取一把锁
        RLock lock = redissonClient.getLock(lockKey);//可重入锁

        /**
         * 1.锁有自动过期时间
         * 2.Redisson的所有操作都是原子的
         * 3.锁可以自动续期
         */
        //2.加锁
        lock.lock();//阻塞式等锁
        //boolean b = lock.tryLock();//尝试加锁,没加上就返回false
        //long waitTime, long leaseTime, TimeUnit unit
        lock.tryLock();

        try {
            System.out.println("执行业务中...");
            TimeUnit.SECONDS.sleep(20);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

        return "ok";
    }
}
