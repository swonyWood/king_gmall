package com.atguigu.gmall.search.repo;

import com.atguigu.gmall.model.list.Goods;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Kingstu
 * @date 2022/7/1 19:39
 */
@Repository
public interface GoodsRepository extends PagingAndSortingRepository<Goods,Long> {
}
