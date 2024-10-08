package com.github.rexliu88.constant;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 资源数据状态
 */
@Slf4j
public class ResDataStatus {
    /**
     * 默认
     * 0 抓取完成
     */
    public static final int DEFAULT = 0;
    /**
     * 0 抓取完成
     */
    public static final int FETCH_COMPLETED = 0;
    /**
     * 1 转换完成
     */
    public static final int TRANSFORM_COMPLETED = 1;
    /**
     * 2 匹配完成
     */
    public static final int MATCHED_COMPLETED = 2;
    /**
     * 3 写入主库完成
     */
    public static final int WRITE_MASTERDB_COMPLETED = 3;

    private static final Map<Object,String> DICT_MAP = new HashMap<>();
    static{
        DICT_MAP.put(FETCH_COMPLETED,"抓取完成");
        DICT_MAP.put(TRANSFORM_COMPLETED,"转换完成");
        DICT_MAP.put(MATCHED_COMPLETED,"匹配完成");
        DICT_MAP.put(WRITE_MASTERDB_COMPLETED,"写入主库完成");
    }

    public static boolean isValid(Integer value){
        if(value == null){
            return false;
        }
        if(!DICT_MAP.containsKey(value)){
            log.warn("来源类型未知，值为：{}",value);
            return false;
        }
        return true;
    }

    private ResDataStatus(){

    }

    public static String getName(Integer value){
        if(isValid(value)){
            return DICT_MAP.get(value);
        }
        return DICT_MAP.get(DEFAULT);
    }
}
