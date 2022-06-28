package com.atguigu.gmall.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 三级分类vo
 * @author Kingstu
 * @date 2022/6/24 12:59
 */
@Data
public class CategoryVo {
    private Long categoryId;
    private String categoryName;
    private List<CategoryVo> categoryChild;
}
