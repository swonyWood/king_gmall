package com.atguigu.gmall.common.config.pool;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author Kingstu
 * @date 2022/7/4 18:55
 */
@Data
@Component
@ConfigurationProperties(prefix = "app.threadpool")
public class ThreadPoolProperties {

    private int corePoolSize = 4;
    private int maximumPoolSize = 8;
    private long keepAliveTime = 60;
    //    private TimeUnit unit = TimeUnit.SECONDS;
    private int workQueueSize = 200;
}
