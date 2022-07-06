package com.atguigu.gmall.product.rpc;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.vo.CategoryVo;
import com.atguigu.gmall.product.biz.CategoryBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * RPC暴露所有和分类有关的远程接口
 * @author Kingstu
 * @date 2022/6/24 13:05
 */
@RestController
@RequestMapping("/rpc/inner/product")
public class CategoryRpcController {

    @Autowired
    CategoryBizService categoryBizService;
    /**
     * 获取系统所有三级分类并组装成树形结构
     * @return
     */
    @GetMapping("/categorys/all")
    public Result<List<CategoryVo>> getCategorys() throws InterruptedException {
        System.out.println("业务处理中...");
        Thread.sleep(2000);
        //查询所有三级分类
        List<CategoryVo> list = categoryBizService.getCategorys();
        return Result.ok(list);
    }
}
