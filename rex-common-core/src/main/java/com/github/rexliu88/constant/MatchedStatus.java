package com.github.rexliu88.constant;

import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 匹配状态:
 * -1 匹配失败\关系解绑
 * 0 未匹配;
 * 1 自动匹配成功;
 * 2 自动匹配失败; 需人工手动匹配    中间
 * 3 人工手动匹配
 */
@Slf4j
public final class MatchedStatus {
    /**
     * 默认
     * 0 未匹配
     */
    public static final int DEFAULT = 0;
    /**
     * 0 未匹配
     */
    public static final int UN_MATCHED = 0;
    /**
     * 1 自动匹配
     */
    public static final int MATCHED = 1;
    /**
     * -1 匹配失败/关系解绑
     */
    public static final int MATCHED_FAILED = -1;
    /**
     * 3 人工匹配
     */
    public static final int MANUAL_MATCHED = 2;
    private static final Map<Object,String> DICT_MAP = new HashMap<>();
    static{
        DICT_MAP.put(UN_MATCHED,"未匹配");
        DICT_MAP.put(MATCHED,"自动匹配");
        DICT_MAP.put(MATCHED_FAILED,"匹配失败/关系解绑");
        DICT_MAP.put(MANUAL_MATCHED,"人工匹配");
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

    private MatchedStatus(){

    }

    public static String getName(Integer value){
        if(isValid(value)){
            return DICT_MAP.get(value);
        }
        return DICT_MAP.get(DEFAULT);
    }
}
