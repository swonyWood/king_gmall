package com.atguigu.gmall.product.config.minio;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Kingstu
 * @date 2022/6/21 20:49
 * @description
 */
@Slf4j
@Configuration
public class MinioConfiguration {

    @Autowired
    MinioProperties minioProperties;


    @Bean
    public MinioClient minioClient() throws Exception{
        MinioClient minioClient = new MinioClient(minioProperties.getEndpoint(),
                minioProperties.getAccessKey(),
                minioProperties.getSecretKey());
        String bucketName = minioProperties.getBucketName();
        if (!minioClient.bucketExists(bucketName)) {
            log.info("minio指定的bucket[{}],不存在,正在自动创建",bucketName);
            minioClient.makeBucket(bucketName);
        }
        return minioClient;
    }
}
