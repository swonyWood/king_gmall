package com.atguigu.gmall.search.repo;

import com.atguigu.gmall.search.bean.Person;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author Kingstu
 * @date 2022/7/1 13:12
 */
@Repository
public interface PersonRepository extends PagingAndSortingRepository<Person,Long> {

    //1.查出年龄大于20的人
    List<Person> findAllByAgeGreaterThan(Integer age);

    //2.查出在武汉,且年龄小于21,或者id是5 的人
    List<Person> findAllByAddressLikeAndAgeLessThanOrIdEquals(String address, Integer age, Long id);
}
