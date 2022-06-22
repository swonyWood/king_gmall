package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Kingstu
 * @date 2022/6/21 20:04
 * @description
 */
@RequestMapping("/admin/product")
@RestController
public class FileController {

    @Autowired
    FileService fileService;

    /**
     * 文件上传aa
     * @param file
     * @return
     */
    @PostMapping("/fileUpload")
    public Result upload(@RequestPart MultipartFile file) throws Exception {

        String url = fileService.upload(file);
        return Result.ok(url);
    }
}
