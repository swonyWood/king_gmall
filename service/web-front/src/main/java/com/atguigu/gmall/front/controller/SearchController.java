package com.atguigu.gmall.front.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.feign.search.SearchFeignClient;
import com.atguigu.gmall.model.vo.search.SearchParam;
import com.atguigu.gmall.model.vo.search.SearchResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * 处理检索请求的控制器
 * @author Kingstu
 * @date 2022/7/1 20:53
 */
@Controller
public class SearchController {

    @Autowired
    SearchFeignClient searchFeignClient;

    @GetMapping("/list.html")
    public String listPage(SearchParam param, Model model){

        Result<Map<String,Object>> search = searchFeignClient.search(param);

        Map<String, Object> data = search.getData();

        model.addAllAttributes(data);
        return "list/index";
    }
}
