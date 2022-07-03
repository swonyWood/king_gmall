package com.atguigu.gmall.search.repo;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * 启动前安装好ik分词器
 * @author Kingstu
 * @date 2022/7/1 19:39
 */
@Repository
public interface GoodsRepository extends PagingAndSortingRepository<Goods,Long> {
}
