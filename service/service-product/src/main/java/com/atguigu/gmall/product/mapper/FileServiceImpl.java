package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.product.config.minio.MinioProperties;
import com.atguigu.gmall.product.service.FileService;
import io.minio.MinioClient;
import io.minio.PutObjectOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.UUID;

/**
 * @author Kingstu
 * @date 2022/6/21 20:46
 * @description
 */
@Service
public class FileServiceImpl implements FileService {

    @Autowired
    MinioClient minioClient;
    @Autowired
    MinioProperties minioProperties;
    @Value("${app.minio.bucketName}")
    String bucketName;

    @Override
    public String upload(MultipartFile file) throws Exception {
        String filename = file.getOriginalFilename();
        filename = UUID.randomUUID().toString().replace("-", "")+"_"+filename;

        //上传
        PutObjectOptions options = new PutObjectOptions(file.getSize(), -1);
        options.setContentType("image/jpeg");
        minioClient.putObject(bucketName,filename, file.getInputStream(),options);
        //返回路径
        String url = minioProperties.getEndpoint()+"/"+minioProperties.getBucketName()+"/"+filename;
        return url;
    }
}
