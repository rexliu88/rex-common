package com.github.rexliu88.constant;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据源对应主库的项目类型
 *
 * 1：雷速足球
 * 2：雷速篮球
 */
@Slf4j
@NoArgsConstructor
public class ResSourceItem {
    private static final Map<Object, Item> DICT_MAP = new HashMap<>();
    static{
        DICT_MAP.put(1,Item.football);   //雷速足球
        DICT_MAP.put(2,Item.basketball);  //雷速篮球
    }
    public static Item getName(Integer value){
        if(value == null){
            return null;
        }
        if(isValid(value)){
            return DICT_MAP.get(value);
        }
        return null;
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
}
