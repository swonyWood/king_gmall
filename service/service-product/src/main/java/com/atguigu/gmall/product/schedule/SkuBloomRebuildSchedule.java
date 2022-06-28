package com.atguigu.gmall.product.schedule;

import com.atguigu.gmall.product.service.BloomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * @author Kingstu
 * @date 2022/6/28 12:52
 */
@Service
public class SkuBloomRebuildSchedule {

    @Autowired
    BloomService bloomService;

    //定时任务
    //*秒 *分 *时 *日 *月 *周
    @Scheduled(cron = "0 0 3 */7 * ?")
    //@Scheduled(cron = "*/3 * * * * ?")
    public void rebuild(){
//        System.out.println("hello");
        bloomService.rebuildSkuBloom();
    }
}
