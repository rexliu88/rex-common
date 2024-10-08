package com.github.rexliu88.common;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import com.alibaba.fastjson.JSON;

import java.util.List;

public class LocalCache {
    /** 单位 秒 */
    private static final Long SECONDS =  1000L;
    /** 单位 分钟 */
    private static final Long MINUTES =  60 * 1000L;
    /** 默认缓存时长 5 分钟 */
    private static final Long DEFAULT_TIMEOUT = 3 * MINUTES;

    /** 默认清理间隔时间 3分钟 */
    private static final Long CLEAN_TIMEOUT = 5 * MINUTES;

    public static TimedCache<String, String> timedCache = CacheUtil.newTimedCache(DEFAULT_TIMEOUT);

    static {
        //启动定时任务
        timedCache.schedulePrune(CLEAN_TIMEOUT);
    }

    /** 缓存前缀 */
    public static final String LOCAL_CACHE_PREFIX = "local:";

    public static <T> void add(String key, T value) {
        timedCache.put(LOCAL_CACHE_PREFIX + key, JSON.toJSONString(value));
    }

    public static <T> void add(String key, T value, Long timeoutSeconds) {
        timedCache.put(LOCAL_CACHE_PREFIX + key, JSON.toJSONString(value), timeoutSeconds * SECONDS);
    }

    public static <T> T get(String key, Class<T> clazz) {
        // 如果用户在超时前调用了get(key)方法，会重头计算起始时间，false的作用就是不从头算
        String jsonString = timedCache.get(LOCAL_CACHE_PREFIX + key, true);
        return JSON.parseObject(jsonString, clazz);
    }

    public static <T> List<T> getList(String key, Class<T> clazz) {
        // 如果用户在超时前调用了get(key)方法，会重头计算起始时间，false的作用就是不从头算
        String jsonString = timedCache.get(LOCAL_CACHE_PREFIX + key, true);
        return JSON.parseArray(jsonString, clazz);
    }

    public static void add(String key, String value) {
        timedCache.put(LOCAL_CACHE_PREFIX + key, value);
    }

    public static String get(String key){
        return timedCache.get(LOCAL_CACHE_PREFIX + key, true);
    }

    public static void remove(String... key) {
        if (key.length > 0) {
            for (String itemKey : key) {
                timedCache.remove(LOCAL_CACHE_PREFIX + itemKey);
            }
        }
    }
}
