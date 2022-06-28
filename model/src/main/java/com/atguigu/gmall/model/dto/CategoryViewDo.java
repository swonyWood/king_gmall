package com.atguigu.gmall.model.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName category_view
 */
@TableName(value ="category_view")
@Data
public class CategoryViewDo implements Serializable {
    /**
     * 编号
     */
    @TableField("id")
    private Long id;

    /**
     * 分类名称
     */
    @TableField("name")
    private String name;

    /**
     * 编号
     */
    @TableField("c2id")
    private Long c2id;

    /**
     * 二级分类名称
     */
    @TableField("c2name")
    private String c2name;

    /**
     * 编号
     */
    @TableField("c3id")
    private Long c3id;

    /**
     * 三级分类名称
     */
    @TableField("c3name")
    private String c3name;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}