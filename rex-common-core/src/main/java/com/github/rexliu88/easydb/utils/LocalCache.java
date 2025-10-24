package com.github.rexliu88.easydb.utils;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.TimedCache;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;

import java.util.List;

/**
 * 本地缓存工具
 * 基于 Hutool 的 TimedCache 实现，
 * 默认缓存时长 3 分钟，对电商类网站有效，但是对管理操作类，需要考虑至少2段式来延长缓存时长使用。
 * 提供带过期时间的缓存功能。
 * 支持缓存对象，获取缓存项、删除缓存项等操作，并支持自定义缓存时长。
 */
public class LocalCache {
    /** 单位 秒 */
    private static final Long SECONDS =  1000L;
    /** 单位 分钟 */
    private static final Long MINUTES =  60 * 1000L;
    /** 默认缓存时长 3 分钟 */
    private static final Long DEFAULT_TIMEOUT = 3 * MINUTES;

    /** 默认清理间隔时间 5 分钟 */
    private static final Long CLEAN_TIMEOUT = 5 * MINUTES;

    /**
     * 定时缓存实例，默认缓存时长 3 分钟
     */
    public static TimedCache<String, String> timedCache = CacheUtil.newTimedCache(DEFAULT_TIMEOUT);

    static {
        //启动定时任务，每5分钟清理一次过期缓存
        timedCache.schedulePrune(CLEAN_TIMEOUT);
    }

    /** 缓存前缀 */
    public static final String LOCAL_CACHE_PREFIX = "local:";

    /**
     * 添加缓存项，默认使用默认超时时间
     *
     * @param key   缓存键
     * @param value 缓存值，将被序列化为JSON字符串存储
     * @param <T>   值的类型
     */
    public static <T> void add(String key, T value) {
        timedCache.put(LOCAL_CACHE_PREFIX + key, JSON.toJSONString(value));
    }

    /**
     * 添加缓存项，指定超时时间（单位：秒）
     *
     * @param key             缓存键
     * @param value           缓存值，将被序列化为JSON字符串存储
     * @param timeoutSeconds  超时时间，单位为秒
     * @param <T>             值的类型
     */
    public static <T> void add(String key, T value, Long timeoutSeconds) {
        timedCache.put(LOCAL_CACHE_PREFIX + key, JSON.toJSONString(value), timeoutSeconds * SECONDS);
    }

    /**
     * 获取缓存项并反序列化为目标对象
     *
     * @param key   缓存键
     * @param clazz 目标类型的Class对象
     * @param <T>   返回值的类型
     * @return 反序列化后的对象，若不存在或已过期则可能返回null
     */
    public static <T> T get(String key, Class<T> clazz) {
        // 如果用户在超时前调用了get(key)方法，会重头计算起始时间，false的作用就是不从头算
        String jsonString = timedCache.get(LOCAL_CACHE_PREFIX + key, true);
        return JSON.parseObject(jsonString, clazz);
    }

    /**
     * 获取缓存项并反序列化为List集合
     *
     * @param key   缓存键
     * @param clazz List中元素的类型Class对象
     * @param <T>   List中元素的类型
     * @return 反序列化后的List集合，若不存在或已过期则返回null
     */
    public static <T> List<T> getList(String key, Class<T> clazz) {
        // 如果用户在超时前调用了get(key)方法，会重头计算起始时间，false的作用就是不从头算
        String jsonString = timedCache.get(LOCAL_CACHE_PREFIX + key, true);
        if(StrUtil.isBlank(jsonString)) {
            return null;
        }
        return JSON.parseArray(jsonString, clazz);
    }

    /**
     * 添加字符串类型的缓存项
     *
     * @param key   缓存键
     * @param value 字符串类型的缓存值
     */
    public static void add(String key, String value) {
        timedCache.put(LOCAL_CACHE_PREFIX + key, value);
    }

    /**
     * 获取字符串类型的缓存项
     *
     * @param key 缓存键
     * @return 缓存值，若不存在或已过期则返回null
     */
    public static String get(String key){
        return timedCache.get(LOCAL_CACHE_PREFIX + key, true);
    }

    /**
     * 删除一个或多个缓存项
     *
     * @param key 一个或多个缓存键
     */
    public static void remove(String... key) {
        if (key.length > 0) {
            for (String itemKey : key) {
                timedCache.remove(LOCAL_CACHE_PREFIX + itemKey);
            }
        }
    }
}
