package com.atguigu.gmall.product.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author Kingstu
 * @date 2022/6/21 20:46
 * @description
 */
public interface FileService {
    /**
     * 文件上传
     * @param file
     * @return
     */
    String upload(MultipartFile file) throws Exception;
}
