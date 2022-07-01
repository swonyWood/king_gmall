package com.atguigu.gmall.search.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

/**
 * @author Kingstu
 * @date 2022/7/1 13:13
 */
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(indexName="person")//启动是在es中创建一个索引库
public class Person {

    @Id //主键
    private Long id;

    @Field("username")
    private String username;

    @Field("email")
    private String email;

    @Field("age")
    private Integer age;

    @Field("address")
    private String address;
}
