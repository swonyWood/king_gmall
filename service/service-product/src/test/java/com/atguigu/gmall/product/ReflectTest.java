package com.atguigu.gmall.product;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * @author Kingstu
 * @date 2022/6/29 10:59
 */

public class ReflectTest {

    @Test
    void test01(){
        Person person = new Person();
        for (Method method : person.getClass().getMethods()) {
            if (method.getName().equals("getPerson")) {
                System.out.println(method.getName()+": 返回值类型"+method.getGenericReturnType());
            }
        }
    }
}
