package com.atguigu.gmall.seckill.schedule;

import com.atguigu.gmall.common.util.DateUtil;
import com.atguigu.gmall.seckill.service.SeckillBizService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.format.DateTimeFormatters;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.swing.text.DateFormatter;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * @author Kingstu
 * @date 2022/7/13 19:37
 */
@Slf4j
@EnableScheduling
@Service
public class SeckillGoodsUpTask {

    @Autowired
    SeckillBizService seckillBizService;

    //每天22.30上架秒杀商品
    @Scheduled(cron = "0 30 22 * * ?")
//    @Scheduled(cron = "0 * * * * ?")
    public void seckillGoodsUp(){
        log.info("定时任务正在上架第二天参与秒杀的所有商品");

        String day = DateUtil.formatDate(new Date());
        //把指定日期的商品提前放到缓存

//        String day = getNextDay();
        seckillBizService.uploadSeckillGoods(day);
    }


    public String getNextDay(){
        LocalDate date = LocalDate.now();
        LocalDate nextDate = date.plus(1L, ChronoUnit.DAYS);

        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String format = nextDate.format(pattern);
        return format;
    }

}
