package com.atguigu.gmall.item;

import org.junit.jupiter.api.Test;
import org.springframework.util.StopWatch;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Kingstu
 * @date 2022/6/30 12:40
 */
public class StreamAPI {

    public static void main2(String[] args) throws ExecutionException, InterruptedException {
        //1.查询价格
        CompletableFuture<BigDecimal> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("正在查询价格: " + Thread.currentThread().getName());
            int i = 199 / 0;
            return new BigDecimal("199");
        }).exceptionally(throwable -> {
            return new BigDecimal(9999);
        });

        //void accept(T t, U u);
        //2.编排-感知异步异常和状态(相当于异步的try..catch)
//        future.whenComplete((x,y)->{
//            System.out.println("正常结果: "+x);
//            System.out.println("异常结果: "+y);
//            if (y!=null) {
//                //兜底业务..
//            }
//        });

        //3.编排-错误兜底(当异步发生异常以后)

        System.out.println("最终价格: "+future.get());
        Thread.currentThread().join();
    }

    public static void main(String[] args) throws Exception {
        StopWatch watch = new StopWatch();
        watch.start();
        System.out.println("开始"+Thread.currentThread().getName());
        //立即返回一个future
        //1.查询基本信息
        CompletableFuture<String> baseInfoFuture = CompletableFuture.supplyAsync(() -> {
            //其他线程
            System.out.println("查询出基本信息"+Thread.currentThread().getName());
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("基本信息查询完成"+Thread.currentThread().getName());
            return "小米8";
        });

//        baseInfoFuture.thenApply();//用上一个人的线程,有形参,有返回值
//        baseInfoFuture.thenAccept();//用上一个人的线程,有形参,无返回值
//        baseInfoFuture.thenRun(); //用上一个人的线程,无形参,无返回值

        //2.基本信息查完,要查价格
        CompletableFuture<BigDecimal> future = baseInfoFuture.thenApply(val -> {
            //接收上一个异步的结果
            System.out.println("拿到了上一个商品的id" + 1);
            System.out.println("正在查询一号商品的价格");
            return new BigDecimal("1999");
        });

        System.out.println(future.get());


//        //2.查询价格
//
//        CompletableFuture<BigDecimal> decimalFuture = CompletableFuture.supplyAsync(() -> {
//            System.out.println("正在查询价格: "+Thread.currentThread().getName());
//            int i = 199/0;
//            return new BigDecimal("199");
//        });

        //3.查询图片
        CompletableFuture<String> imageFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("正在查询图片: "+Thread.currentThread().getName());
            return "girl1.jpg";
        });

        //4.查询销售属性
        CompletableFuture<String> attrFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("正在查询销售属性: "+Thread.currentThread().getName());
            return "8+128";
        });

        //4.其他
        //3.查询图片
        CompletableFuture<String> otherFuture = CompletableFuture.supplyAsync(() -> {
            System.out.println("正在查询兄弟组合: "+Thread.currentThread().getName());
            return "还有8+256等等";
        });

        //1.编排-所有都完成
//        CompletableFuture.allOf(baseInfoFuture,decimalFuture,imageFuture,attrFuture,otherFuture).get();





//        System.out.println("商品信息: 商品: "+baseInfoFuture.get()+"价格: "+decimalFuture.get()+"图片: "+imageFuture.get()
//        +"销售属性: "+attrFuture.get()+"兄弟组合: "+otherFuture.get());


        watch.stop();

        System.out.println("耗时: "+watch.getTotalTimeMillis());

        System.out.println("结束"+Thread.currentThread().getName());
        Thread.currentThread().join();

    }

    @Test
    void compleTableFutureTest(){
        //1.启动一个异步任务
        //2.得到异步结果
        //3.怎么编排


    }


    /**
     * 流式编程
     */
    @Test
    void test01(){
        //1.一堆数据
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8);

        //2.找到这堆数据中的偶数,*2

        //3.把>10的偶数不要

        //4.把剩下的偶数求个和
        Integer sum=0;
        for (Integer integer : list) {
            if (integer%2 ==0) {
                integer *= 2;
                if (integer <= 10) {
                    sum += integer;
                }
            }
        }
        System.out.println("sum = " + sum);
    }

    @Test
    void test02(){
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8);


        Integer integer = list.stream()
                .parallel()
                .filter(o -> {
                    System.out.println(o+"元素,正在被线程"+Thread.currentThread().getName()+"处理");
                    return o % 2 == 0;
                })
                .map(t -> t * 2)
                .filter(o -> o <= 10)
                .reduce((v1, v2) -> v1 + v2)
                .get();
        System.out.println(integer);

    }

    @Test
    void testFunction(){
        //Predicate
        Predicate<Integer> predicate = new Predicate<Integer>() {
            @Override
            public boolean test(Integer integer) {
                return integer % 2==0;
            }
        };

        System.out.println(predicate.test(12));//true
        System.out.println(predicate.test(7));//false
        //lambda表达式简写函数式接口
        Predicate<Integer> predicate2 = (Integer integer)->{
            return integer % 2==0;
        };

        Predicate<Integer> predicate3 = o -> o % 2==0;
    }
}
