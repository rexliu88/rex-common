package com.github.rexliu88.constant;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 确认状态
 * -1  未匹配上
 *  0  未确认
 *  1  已确认
 */
@Slf4j
public final class ConfirmStatus {
    /**
     * 默认
     * 0 未确认
     */
    public static final int DEFAULT = 0;
    /**
     * 未匹配上
     */
    public static final int NOT_MATCHED = -1;
    /**
     * 0 未确认
     */
    public static final int UN_CONFIRMED = 0;
    /**
     * 1 已确认
     */
    public static final int CONFIRMED = 1;
    private static final Map<Object,String> DICT_MAP = new HashMap<>();
    static{
        DICT_MAP.put(UN_CONFIRMED,"未确认");
        DICT_MAP.put(CONFIRMED,"已确认");
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

    private ConfirmStatus(){

    }

    public static String getName(Integer value){
        if(isValid(value)){
            return DICT_MAP.get(value);
        }
        return DICT_MAP.get(DEFAULT);
    }
}
