package com.atguigu.gmall.order.service.impl;
import java.util.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.atguigu.gmall.common.constant.MqConst;
import com.atguigu.gmall.common.util.AuthContextHolder;
import com.atguigu.gmall.model.activity.CouponInfo;

import java.math.BigDecimal;
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
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.model.order.OrderStatusLog;
import com.atguigu.gmall.model.to.mq.OrderCreateMsg;
import com.atguigu.gmall.model.to.mq.WareStockDetail;
import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.model.vo.order.CartOrderDetailVo;
import com.atguigu.gmall.model.vo.order.OrderSubmitVo;
import com.atguigu.gmall.model.vo.user.UserAuth;
import com.atguigu.gmall.model.vo.ware.OrderSpiltVo;
import com.atguigu.gmall.model.vo.ware.OrderSplitRespVo;
import com.atguigu.gmall.model.vo.ware.WareFenBuVo;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderDetailService;
import com.atguigu.gmall.order.service.OrderInfoService;
import com.atguigu.gmall.order.service.OrderStatusLogService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.collect.Lists;

import com.atguigu.gmall.model.vo.order.OrderConfirmVo;
import com.atguigu.gmall.order.service.OrderBizService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
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
    OrderDetailService orderDetailService;

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

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    OrderInfoMapper orderInfoMapper;

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

        //6.发消息给MQ //orderId, userId,totalAmout,status
        OrderCreateMsg msg = prepareOrderMsg(info);
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_ORDER_EVENT, MqConst.RK_ORDER_CREATE,JSONs.toStr(msg));



        return info.getId();
    }

    //准备订单创建的消息数据
    private OrderCreateMsg prepareOrderMsg(OrderInfo info) {
        UserAuth auth = AuthContextHolder.getUserAuth();
        OrderCreateMsg msg = new OrderCreateMsg(info.getId(),auth.getUserId(),info.getTotalAmount(),info.getOrderStatus());
        return msg;
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

    @Transactional
    @Override
    public void closeOrder(Long orderId, Long userId) {
        ProcessStatus closeStatus = ProcessStatus.CLOSED;
        //1.修改订单状态
        orderInfoService.updateOrderStatus(orderId,userId,closeStatus.getOrderStatus().name(),
                closeStatus.name(),ProcessStatus.UNPAID.name());


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

    @Override
    public OrderInfo getOrderInfoAndUserId(Long id) {

        Long userId = AuthContextHolder.getUserAuth().getUserId();
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id",userId);
        wrapper.eq("id",id);
        List<OrderInfo> infos = orderInfoMapper.selectList(wrapper);
        if (infos==null||infos.size() == 0) {
            return null;
        }
        return infos.get(0);
    }

    @Transactional
    @Override
    public List<OrderSplitRespVo> splitOrder(OrderSpiltVo vo) {
        List<OrderSplitRespVo> result = new ArrayList<>();
        //1.查询当前订单以及详情
        OrderInfo orderInfo = orderInfoService.getOrderInfoAndDetails(Long.parseLong(vo.getOrderId()),
                Long.parseLong(vo.getUserId()));

        //2、按照库存分布拆分出新单
        String skuMapJson = vo.getWareSkuMap();
        //3、得到库存分布信息
        List<WareFenBuVo> fenBuVos = JSONs.toObj(skuMapJson, new TypeReference<List<WareFenBuVo>>() {
        });

        //4、按照仓库拆单
        for (WareFenBuVo buVo : fenBuVos) {
            //保存子订单
            OrderSplitRespVo order = saveChildOrder(orderInfo, buVo);
            result.add(order);
        }

        //5、父单改为已拆分状态
        ProcessStatus split = ProcessStatus.SPLIT;
        //也会推进日志
        orderInfoService.updateOrderStatus(orderInfo.getId(),
                orderInfo.getUserId(),
                split.getOrderStatus().name(),
                split.name(),
                ProcessStatus.PAID.name());


        return result;
    }

    @Transactional
    @Override
    public OrderSplitRespVo saveChildOrder(OrderInfo orderInfo, WareFenBuVo buVo) {
        //1、准备子订单
        OrderInfo childOrder = prepareChildOrder(orderInfo,buVo);

        //2、保存子订单 order_info
        orderInfoService.save(childOrder);

        //3、保存子订单详情
        List<OrderDetail> detailList =
                childOrder.getOrderDetailList().stream().map(item->{
                    item.setOrderId(childOrder.getId()); //设置号子单id即可
                    return item;
                }).collect(Collectors.toList());
        orderDetailService.saveBatch(detailList);


        //4、准备返回
        return prepareOrderSplitRespVo(childOrder);

    }

    private OrderSplitRespVo prepareOrderSplitRespVo(OrderInfo childOrder) {
        OrderSplitRespVo respVo = new OrderSplitRespVo();
        respVo.setOrderId(childOrder.getId()+"");
        respVo.setConsignee(childOrder.getConsignee());
        respVo.setConsigneeTel(childOrder.getConsigneeTel());
        respVo.setOrderComment(childOrder.getOrderComment());
        respVo.setOrderBody(childOrder.getTradeBody());
        respVo.setDeliveryAddress(childOrder.getDeliveryAddress());
        respVo.setPaymentWay("2");
        respVo.setWareId(childOrder.getWareId());

        //子单负责的所有商品详情
        //List<WareStockDetail>
        List<WareStockDetail> details = childOrder.getOrderDetailList().stream()
                .map(item -> new WareStockDetail(item.getSkuId(), item.getSkuNum(), item.getSkuName()))
                .collect(Collectors.toList());
        respVo.setDetails(details);

        return respVo;
    }

    private OrderInfo prepareChildOrder(OrderInfo orderInfo, WareFenBuVo buVo) {
        OrderInfo childOrder = new OrderInfo();

        //设置订单详情；
        Set<String> skuIds = buVo.getSkuIds().stream().collect(Collectors.toSet());
        //获取总单中子订单负责的商品
        List<OrderDetail> orderDetails = orderInfo.getOrderDetailList().stream()
                .filter(item -> skuIds.contains(item.getSkuId().toString()))
                .collect(Collectors.toList());
        //得到当前子订单负责的商品
        childOrder.setOrderDetailList(orderDetails);

        childOrder.setConsignee(orderInfo.getConsignee());
        childOrder.setConsigneeTel(orderInfo.getConsigneeTel());

        //拆单后包含的商品的总额
        BigDecimal total = orderDetails.stream()
                .map(item -> item.getOrderPrice().multiply(new BigDecimal(item.getSkuNum())))
                .reduce((o1, o2) -> o1.add(o2))
                .get();
        childOrder.setTotalAmount(total);


        //每个子单自己购买的商品的名字
        childOrder.setTradeBody(orderDetails.get(0).getSkuName());
        //每个子单自己购买的商品的图片
        childOrder.setImgUrl(orderDetails.get(0).getImgUrl());
        //子单拆分的时间
        childOrder.setCreateTime(new Date());

        //子单涉及到的商品原始总额
        childOrder.setOriginalTotalAmount(new BigDecimal("0"));
        //子单每个商品自己的优惠额
        childOrder.setActivityReduceAmount(new BigDecimal("0"));
        childOrder.setCouponAmount(new BigDecimal("0"));

        //每个子单最终配送以后会有自己的物流号
        childOrder.setTrackingNo("");

        childOrder.setOperateTime(new Date());

        childOrder.setOrderStatus(orderInfo.getOrderStatus());
        childOrder.setUserId(orderInfo.getUserId());
        childOrder.setPaymentWay(orderInfo.getPaymentWay());
        childOrder.setDeliveryAddress(orderInfo.getDeliveryAddress());
        childOrder.setOrderComment(orderInfo.getOrderComment());
        childOrder.setOutTradeNo(orderInfo.getOutTradeNo());
        childOrder.setExpireTime(orderInfo.getExpireTime());
        childOrder.setProcessStatus(orderInfo.getProcessStatus());
        childOrder.setParentOrderId(orderInfo.getParentOrderId());
        childOrder.setWareId(buVo.getWareId());
        childOrder.setRefundableTime(orderInfo.getRefundableTime());
        childOrder.setFeightFee(new BigDecimal("0"));



        return childOrder;
    }
}











