package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.dto.CategoryViewDo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.CategoryViewService;
import com.atguigu.gmall.product.mapper.CategoryViewMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 */
@Service
public class CategoryViewServiceImpl extends ServiceImpl<CategoryViewMapper, CategoryViewDo>
    implements CategoryViewService{
    @Autowired
    CategoryViewMapper categoryViewMapper;

    @Override
    public CategoryViewDo getViewByC3Id(Long c3Id) {
        QueryWrapper<CategoryViewDo> wrapper = new QueryWrapper<>();
        wrapper.eq("c3id",c3Id);
        CategoryViewDo categoryViewDo = categoryViewMapper.selectOne(wrapper);
        return categoryViewDo;
    }
}




