package com.atguigu.gmall.product;

import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import io.minio.errors.MinioException;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.ibatis.annotations.Options;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.FileInputStream;

/**
 * @author Kingstu
 * @date 2022/6/21 20:18
 * @description
 */
@SpringBootTest
public class MinioTest {

    @Autowired
    MinioClient minioClient;
    @Test
    void testUpload2()throws Exception{
        FileInputStream fis = new FileInputStream("d:\\iuser\\Saved Pictures\\素材\\4.jpg");
        PutObjectOptions options = new PutObjectOptions(fis.available(), -1L);
        options.setContentType("image/jpeg");
        minioClient.putObject("gmall", "4.jpg",fis,options );
    }


    @Test
    void testUpload(){
        try {
            // 使用MinIO服务的URL，端口，Access key和Secret key创建一个MinioClient对象
            MinioClient minioClient = new MinioClient("http://192.168.200.100:9000",
                    "admin",
                    "admin123456");
            System.out.println(minioClient);

            // 检查存储桶是否已经存在
            boolean isExist = minioClient.bucketExists("gmall");
            if(!isExist) {
                // 创建一个名为asiatrip的存储桶，用于存储照片的zip文件。
                minioClient.makeBucket("gmall");
                System.out.println("桶不存在,先已经创建");
            }

            //上传
            FileInputStream fis = new FileInputStream("d:\\iuser\\Saved Pictures\\素材\\1.jpg");
            PutObjectOptions options = new PutObjectOptions(fis.available(), -1L);
            options.setContentType("image/jpeg");
            minioClient.putObject("gmall","1.jpg",fis,options);
            System.out.println("over");
        } catch(Exception e) {
            System.out.println("Error occurred: " + e);
        }
    }
}
