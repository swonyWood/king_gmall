package com.atguigu.gmall.search;

import com.atguigu.gmall.search.bean.Person;
import com.atguigu.gmall.search.repo.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * @author Kingstu
 * @date 2022/7/1 13:21
 */
@SpringBootTest
public class EsRepositoryTest {


    @Autowired
    PersonRepository personRepository;

    @Autowired
    ElasticsearchRestTemplate esRestTemplate;

    @Test
    void testQuery3(){
        //自定义检索操作
        //esRestTemplate.search();
        //自定义保存操作
//        esRestTemplate.index();
        //自定义删除操作
//        esRestTemplate.delete();
        //自定义修改操作
//        esRestTemplate.update();

        //批量操作
//        esRestTemplate.bulkIndex();

    }


    @Test
    void testQuery2(){
        List<Person> all = personRepository.findAllByAddressLikeAndAgeLessThanOrIdEquals("武", 20, 3L);
        all.stream().forEach(o-> System.out.println(o));
    }
    @Test
    void testQuery(){
        List<Person> persons = personRepository.findAllByAgeGreaterThan(20);
        persons.stream().forEach(o-> System.out.println(o));
    }



    @Test
    void saveMulti(){

        List<Person> people = Arrays.asList(
                new Person(1L, "李四1", "aaa1@qq.com", 18, "武汉市1"),
                new Person(2L, "李四2", "aaa2@qq.com", 19, "武汉市2"),
                new Person(3L, "李四3", "aaa3@qq.com", 20, "武汉市3"),
                new Person(4L, "李四4", "aaa4@qq.com", 21, "武汉市4"),
                new Person(5L, "李四5", "aaa5@qq.com", 22, "武汉市5"));

        personRepository.saveAll(people);
    }

    /**
     * 自定义复杂操作: 自己发rest请求,自己拼json请求体
     */
    @Test
    void testUpdate(){
        Person person = new Person();
        person.setId(1L);
        person.setUsername("张三1");

        //age不带,age是null,es会用null覆盖
        personRepository.save(person);

        //rest
    }

    @Test
    void testPersonRepository(){

        Person person = new Person();
        person.setId(1L);
        person.setUsername("张三");
        person.setEmail("111@qq.com");
        person.setAge(18);
        person.setAddress("武汉市");


        personRepository.save(person);
        System.out.println("保存成功");
        Optional<Person> byId = personRepository.findById(1L);
        System.out.println("查询结果: "+byId.get());
    }
}
