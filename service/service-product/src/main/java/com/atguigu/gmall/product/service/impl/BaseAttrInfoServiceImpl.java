package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseAttrValue;
import com.atguigu.gmall.product.service.BaseAttrValueService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.atguigu.gmall.product.service.BaseAttrInfoService;
import com.atguigu.gmall.product.mapper.BaseAttrInfoMapper;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
@Service
public class BaseAttrInfoServiceImpl extends ServiceImpl<BaseAttrInfoMapper, BaseAttrInfo>
    implements BaseAttrInfoService{

    @Autowired
    BaseAttrInfoMapper baseAttrInfoMapper;

    @Autowired
    BaseAttrValueService baseAttrValueService;

    @Override
    public List<BaseAttrInfo> getBaseAttrInfoWihtValue(Long c1Id, Long c2Id, Long c3Id) {
        return baseAttrInfoMapper.getBaseAttrInfoWihtValue(c1Id,c2Id,c3Id);
    }

    @Transactional
    @Override
    public void saveAttrAndValue(BaseAttrInfo baseAttrInfo) {
        //1.保存属性名信息
        baseAttrInfoMapper.insert(baseAttrInfo);

        //mybatisplus自动回填自增的主键到原javaBean
        Long id = baseAttrInfo.getId();
        //2.保存属性值信息
        List<BaseAttrValue> values = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue value : values) {
            value.setAttrId(id);
        }

        //批量保存
        baseAttrValueService.saveBatch(values);

    }

    @Override
    public void updateAttrAndValue(BaseAttrInfo baseAttrInfo) {
        //1.修改属性名
        baseAttrInfoMapper.updateById(baseAttrInfo);

        //2. 修改属性值
        List<Long> ids = new ArrayList<>();
        List<BaseAttrValue> valueList = baseAttrInfo.getAttrValueList();
        for (BaseAttrValue value : valueList) {
            //2.1 新增(没带id直接新增)
            if (value.getId()==null) {
                value.setAttrId(baseAttrInfo.getId());//原本值没关联的要关联
                baseAttrValueService.save(value);
            }
            //2.2 修改
            if (value.getId()!=null) {
                baseAttrValueService.updateById(value);
                ids.add(value.getId());
            }

        }

        if (ids.size() > 0) {
            //2.3 删除(前端没带的id的值)
            //delete * from base_attr_value where attr_id=12 and id is not in (60,61)
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id", baseAttrInfo.getId());
            wrapper.notIn("id", ids);//不在前端携带中
            baseAttrValueService.remove(wrapper);
        }else{
            QueryWrapper<BaseAttrValue> wrapper = new QueryWrapper<>();
            wrapper.eq("attr_id", baseAttrInfo.getId());
            baseAttrValueService.remove(wrapper);
        }
    }


}




