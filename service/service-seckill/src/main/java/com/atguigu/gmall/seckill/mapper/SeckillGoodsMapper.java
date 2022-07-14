package com.atguigu.gmall.seckill.mapper;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Entity com.atguigu.gmall.seckill.domain.SeckillGoods
 */
public interface SeckillGoodsMapper extends BaseMapper<SeckillGoods> {

    /**
     * 查询指定某天参与秒杀的所有商品
     * @param day
     * @return
     */
    List<SeckillGoods> getDaySeckillGoods(@Param("day") String day);

    /**
     * 删除指定秒杀商品的库存
     * @param skuId
     * @param num
     * @return
     */
    long updateSeckillStockCount(@Param("skuId") Long skuId, @Param("num") int num);
}




