package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/6/21 10:11
 * @description
 */
@Slf4j
@RequestMapping("/admin/product")
@RestController
public class BaseAttrController {

    @Autowired
    BaseAttrInfoService baseAttrInfoService;

    @Autowired
    BaseAttrValueService baseAttrValueService;

    /**
     * 查询分类下的所有属性名
     * @param c1Id
     * @param c2Id
     * @param c3Id
     * @return
     */
    @GetMapping("/attrInfoList/{c1Id}/{c2Id}/{c3Id}")
    public Result attrInfoList(@PathVariable("c1Id") Long c1Id,
                               @PathVariable("c2Id") Long c2Id,
                               @PathVariable("c3Id") Long c3Id){
        List<BaseAttrInfo> infos =  baseAttrInfoService.getBaseAttrInfoWihtValue(c1Id,c2Id,c3Id);
        return Result.ok(infos);
    }

    /**
     * 保存平台属性和值
     * @return
     */
    @PostMapping("/saveAttrInfo")
    public Result saveAttrInfo(@RequestBody BaseAttrInfo baseAttrInfo){
        log.info("保存/修改属性: {}",baseAttrInfo);
        if (baseAttrInfo.getId()!=null) {
            //修改属性
            baseAttrInfoService.updateAttrAndValue(baseAttrInfo);
        }else {
            //保存属性
            baseAttrInfoService.saveAttrAndValue(baseAttrInfo);
        }

        return Result.ok();
    }

    /**
     * 根据id查询属性值
     * @param attrId
     * @return
     */
    @GetMapping("/getAttrValueList/{attrId}")
    public Result getAttrValueList(@PathVariable("attrId") Long attrId){

        List<BaseAttrValue> values = baseAttrValueService.getAttrValueList(attrId);
        return Result.ok(values);
    }

}

































