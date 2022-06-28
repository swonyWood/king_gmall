package com.atguigu.gmall.product.biz;

import com.atguigu.gmall.model.vo.CategoryVo;

import java.util.List;

/**
 * 和分类有关的复杂业务,都封装在biz包下
 * @author Kingstu
 * @date 2022/6/24 13:10
 */
public interface CategoryBizService {
    /**
     * 查询所有三级分类
     * @return
     */
    List<CategoryVo> getCategorys();
}
