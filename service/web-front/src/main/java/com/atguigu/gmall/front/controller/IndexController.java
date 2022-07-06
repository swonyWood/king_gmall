package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.product.CategoryFeignClient;
import com.atguigu.gmall.model.vo.CategoryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/6/24 12:51
 * @description 首页
 */

@Controller
public class IndexController {
    @Autowired
    CategoryFeignClient categoryFeignClient;

    @GetMapping({"/","/index.html"})
    public String indexPage(Model model){
        Result<List<CategoryVo>> categorys = categoryFeignClient.getCategorys();
        List<CategoryVo> data = categorys.getData();
        //查询三级分类数据
        model.addAttribute("list",data);
        return "index/index";
    }
}
