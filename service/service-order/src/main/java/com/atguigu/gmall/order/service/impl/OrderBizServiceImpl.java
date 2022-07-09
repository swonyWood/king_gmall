package com.atguigu.gmall.order.service.impl;
import java.util.Date;

import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.activity.CouponInfo;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.atguigu.gmall.common.constant.RedisConst;
import com.atguigu.gmall.common.execption.GmallException;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.result.ResultCodeEnum;
import com.atguigu.gmall.common.util.JSONs;
import com.atguigu.gmall.feign.cart.CartFeignClient;
import com.atguigu.gmall.feign.product.SkuFeignClient;
import com.atguigu.gmall.feign.user.UserFeignClient;
import com.atguigu.gmall.feign.ware.WareFeignClient;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.order.OrderStatusLog;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.CartOrderDetailVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.model.vo.user.UserAuth;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.OrderStatusLogService;
import com.google.common.collect.Lists;

import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.order.service.OrderBizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * @author Kingstu
 * @date 2022/7/8 20:58
 */
@Service
public class OrderBizServiceImpl implements OrderBizService {

    @Autowired
    OrderBizService orderBizService;

    @Autowired
    UserFeignClient userFeignClient;

    @Autowired
    CartFeignClient cartFeignClient;

    @Autowired
    SkuFeignClient skuFeignClient;

    @Autowired
    WareFeignClient wareFeignClient;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderInfoService orderInfoService;

    @Autowired
    OrderStatusLogService orderStatusLogService;

    @Override
    public OrderConfirmVo getOrderComfirmData() {

        OrderConfirmVo confirmVo = new OrderConfirmVo();

        //用户要透传id
        RequestAttributes old = RequestContextHolder.getRequestAttributes();
        CompletableFuture<Void> userAddressAsync = CompletableFuture.runAsync(() -> {
            //1.地址列表
            RequestContextHolder.setRequestAttributes(old);
            confirmVo.setUserAddressList(userFeignClient.getUserAddress().getData());
            RequestContextHolder.resetRequestAttributes();
        }, executor);


        //2.去获取购物车中选中的商品
        CompletableFuture<List<CartInfo>> checkedCartItemAsync = CompletableFuture
                .supplyAsync(() -> {
                    RequestContextHolder.setRequestAttributes(old);
                    List<CartInfo> data = cartFeignClient.getCheckedCartItem().getData();
                    RequestContextHolder.resetRequestAttributes();
                    return data;
                }, executor);

        //3.处理每个商品价格,库存等信息
        CompletableFuture<List<CartOrderDetailVo>> handlerSkuInfoFuture = checkedCartItemAsync.thenApplyAsync(checkedItems -> {
            List<CartOrderDetailVo> vos = checkedItems.stream()
                    //并行
                    .parallel()
                    .map(info -> {
                        CartOrderDetailVo detailVo = new CartOrderDetailVo();
                        detailVo.setImgUrl(info.getImgUrl());
                        detailVo.setSkuName(info.getSkuName());
                        //实时查价
                        Result<BigDecimal> result = skuFeignClient.get1010SkuPrice(info.getSkuId());
                        detailVo.setOrderPrice(result.getData());
                        detailVo.setSkuNum(info.getSkuNum());
                        detailVo.setSkuId(info.getSkuId());
                        //远程库存
                        String hasStock = wareFeignClient.hasStock(info.getSkuId(), info.getSkuNum());
                        detailVo.setStock(hasStock);
                        return detailVo;
                    }).collect(Collectors.toList());
            //赋值给vo
            confirmVo.setDetailArrayList(vos);
            return vos;
        }, executor);

        //4.总数量
        CompletableFuture<Void> totalNumFuture = checkedCartItemAsync.thenAcceptAsync(items -> {
            Integer totalNum = items.stream()
                    .map(info -> info.getSkuNum())
                    .reduce((o1, o2) -> o1 + o2)
                    .get();
            confirmVo.setTotalNum(totalNum);
        }, executor);


        //4.总金额
        CompletableFuture<Void> totalAmountAsync = handlerSkuInfoFuture.thenAcceptAsync(items -> {
            BigDecimal totalAmount = items.stream()
                    .map(cart -> cart.getOrderPrice().multiply(new BigDecimal(cart.getSkuNum())))
                    .reduce((o1, o2) -> o1.add(o2))
                    .get();
            confirmVo.setTotalAmount(totalAmount);
        }, executor);


        //5.防重令牌
        confirmVo.setTradeNo(generateTradeToken());

        //等异步完成
        CompletableFuture.allOf(userAddressAsync, handlerSkuInfoFuture, totalNumFuture, totalAmountAsync).join();


        return confirmVo;
    }

    @Override
    public String generateTradeToken() {
        //1.生成交易令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        //2.给redis一份/10分钟过期
        redisTemplate.opsForValue().set(RedisConst.TRADE_TOEKN_PREFIX + token, RedisConst.A_KEN_VALUE, 10, TimeUnit.MINUTES);

        return token;
    }

    //原子验证令牌
    @Override
    public boolean checkTradeToken(String token) {

        String script = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long execute = redisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Arrays.asList(RedisConst.TRADE_TOEKN_PREFIX + token),
                RedisConst.A_KEN_VALUE);
        return execute == 1L;//true
    }

    @Override
    public Long submitOrder(String tradeNo, OrderSubmitVo order) {
        //1.验证令牌
        boolean token = checkTradeToken(tradeNo);
        if (!token) {
            throw new GmallException(ResultCodeEnum.ORDER_INVALID_TOKEN);
        }
        //2.验证库存
        List<String> noStockSku = order.getOrderDetailList().stream()
                .filter(item -> {
                    Long skuId = item.getSkuId();
                    Integer skuNum = item.getSkuNum();
                    String stock = wareFeignClient.hasStock(skuId, skuNum);
                    return "0".equals(stock);
                }).map(item -> item.getSkuName())
                .collect(Collectors.toList());


        if (noStockSku != null && noStockSku.size() > 0) {
            throw new GmallException(ResultCodeEnum.ORDER_ITEM_NO_STOCK.getMessage() + JSONs.toStr(noStockSku),
                    ResultCodeEnum.ORDER_ITEM_NO_STOCK.getCode());
        }

        //3.验证价格
        List<String> priceChangeSku = order.getOrderDetailList().stream()
                .filter(item -> {
                    Result<BigDecimal> result = skuFeignClient.get1010SkuPrice(item.getSkuId());
                    return !item.getOrderPrice().equals(result.getData());
                }).map(item -> item.getSkuName())
                .collect(Collectors.toList());
        if (priceChangeSku != null && priceChangeSku.size() > 0) {
            throw new GmallException(ResultCodeEnum.ORDER_ITEM_PRICE_CHANGE.getMessage() + JSONs.toStr(priceChangeSku),
                    ResultCodeEnum.ORDER_ITEM_PRICE_CHANGE.getCode());
        }

        //4.保存订单--事务的方法一定要动态代理调用,this是组件本身,不是代理对象
        //自己注入自己,循环引用,死锁
        //确保是代理对象在调用方法
        OrderInfo info = orderBizService.saveOrder(tradeNo, order);

        //5.删除购物车中这个商品
        cartFeignClient.deleteChecked();

        //6.30min后未支付就关闭订单,支付锁库存
        //异步任务--睡30min
        //线程池开延时任务--任务在内存中
        //分布式延时任务框架--ok
        //MQ--ok
        //异步定时+定时扫描--时效性



        return info.getId();
    }

    //事务内reque.new--内部影响外部事务,外部事务不影响内部
    //事务内不写,默认使用同一个事务,外部的事务会传播到内部,内部的属性不生效
    @Transactional //事务在异步情况下失效
    @Override
    public OrderInfo saveOrder(String tradeNo, OrderSubmitVo order) {
        //1.订单信息保存
        OrderInfo orderInfo = prepareOrderInfo(tradeNo,order);
        orderInfoService.save(orderInfo);

        //2.订单明细保存

        orderInfoService.saveDetail(orderInfo,order);

        //3.订单日志保存

        OrderStatusLog log = prepareOrderStatusLog(orderInfo);
        orderStatusLogService.save(log);


        return orderInfo;
    }
    //准备日志信息
    private OrderStatusLog prepareOrderStatusLog(OrderInfo orderInfo) {

        Long userId = AuthContextHolder.getUserAuth().getUserId();
        OrderStatusLog log = new OrderStatusLog();
        log.setOrderId(orderInfo.getId());
        log.setOrderStatus(orderInfo.getOrderStatus());
        log.setOperateTime(new Date());
        log.setUserId(userId);


        return log;
    }

    //准备订单数据
    private OrderInfo prepareOrderInfo(String tradeNo,OrderSubmitVo vo) {

        OrderInfo orderInfo = new OrderInfo();

        orderInfo.setConsignee(vo.getConsignee());
        orderInfo.setConsigneeTel(vo.getConsigneeTel());

        BigDecimal total = vo.getOrderDetailList().stream()
                .map(item -> item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum())))
                .reduce((o1, o2) -> o1.add(o2))
                .get();
        orderInfo.setTotalAmount(total);



        orderInfo.setOrderStatus(OrderStatus.UNPAID.name());

        Long userId = AuthContextHolder.getUserAuth().getUserId();
        orderInfo.setUserId(userId);

        //线上支付
        orderInfo.setPaymentWay("ONLINE");


        orderInfo.setDeliveryAddress(vo.getDeliveryAddress());

        orderInfo.setOrderComment(vo.getOrderComment());


        orderInfo.setOutTradeNo("ATGUIGU_"+tradeNo+"_"+userId);

        String allName = vo.getOrderDetailList().stream()
                .map(item -> item.getSkuName())
                .reduce((o1, o2) -> o1 + "</br>" + o2)
                .get();

        orderInfo.setTradeBody(allName);

        //创建时间
        orderInfo.setCreateTime(new Date());
        //过期时间:30min
        long m = new Date().getTime() + 1000 * 60 * 30;
        Date date = new Date(m);
        orderInfo.setExpireTime(date);

        //处理状态
        orderInfo.setProcessStatus(ProcessStatus.UNPAID.name());

        //物流id
        orderInfo.setTrackingNo("");

        //订单拆单的父id
        orderInfo.setParentOrderId(0L);

        orderInfo.setImgUrl(vo.getOrderDetailList().get(0).getImgUrl());

        //表中不存在
//        orderInfo.setOrderDetailList(Lists.newArrayList());


//        orderInfo.setWareId("");
        orderInfo.setProvinceId(0L);

        //远程
        orderInfo.setActivityReduceAmount(new BigDecimal("0"));
        orderInfo.setCouponAmount(new BigDecimal("0"));


        orderInfo.setOriginalTotalAmount(total);

        //签收后才确定
        orderInfo.setRefundableTime(new Date());

        //远程
        orderInfo.setFeightFee(new BigDecimal("0"));


        orderInfo.setOperateTime(new Date());
//        orderInfo.setOrderDetailVoList(Lists.newArrayList());
//        orderInfo.setCouponInfo(new CouponInfo());


        return orderInfo;
    }
}











