package com.atguigu.gmall.product.mapper;


import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.model.vo.CategoryVo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/6/20 20:05
 * @description
 */
public interface BaseCategory1Mapper extends BaseMapper<BaseCategory1> {
    List<CategoryVo> getCategorys();
}
