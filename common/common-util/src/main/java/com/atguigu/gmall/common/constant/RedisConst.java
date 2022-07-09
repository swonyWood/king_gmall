package com.atguigu.gmall.common.constant;

/**
 * @author Kingstu
 * @date 2022/6/27 20:45
 */
public class RedisConst {
    public static final String SKU_INFO_CACHE_KEY_PREFIX = "sku:info:";
    public static final String SKU_INFO_LOCK_PREFIX = "lock:sku:info:";
    public static final long SKU_INFO_CACHE_TIMEOUT = 1000*60*60*24*7L;
    public static final String SKU_BLOOM_FILTER_NAME = "boolm:skuid";
    public static final String LOCK_PREFIX = "lock:";
    public static final String SKU_HOTSCORE = "hotscore:";
    public static final String USER_LOGIN_PREFIX = "user:login:";
    public static final String CART_INFO_PREFIX = "cart:info:";
    public static final Integer CART_SIZE_LIMIT = 200;
    public static final String SKU_PRICE_CACHE_PREFIX = "sku:price:";
    public static final String A_KEN_VALUE = "X";
    public static final String TRADE_TOEKN_PREFIX = "trade:token:";
}
