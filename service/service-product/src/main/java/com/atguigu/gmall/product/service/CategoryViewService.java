package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.dto.CategoryViewDo;
import com.atguigu.gmall.model.vo.CategoryView;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 *
 */
public interface CategoryViewService extends IService<CategoryViewDo> {

    /**
     * 按照三级分类id查出所在的完整分类信息
     * @param c3Id
     * @return
     */
    CategoryViewDo getViewByC3Id(Long c3Id);
}
