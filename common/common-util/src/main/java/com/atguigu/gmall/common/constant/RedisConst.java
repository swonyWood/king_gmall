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
}
