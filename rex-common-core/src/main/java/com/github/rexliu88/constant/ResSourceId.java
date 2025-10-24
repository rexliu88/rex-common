package com.github.rexliu88.constant;

import com.github.rexliu88.dto.DictItemDto;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资源数据源
 * 0 未知数据源
 * 1 雷速足球
 * 2 雷速篮球
 * 3 夸克足球
 * 4 夸克篮球
 *
 * 100 自身数据
 */
@Slf4j
public class ResSourceId {
    /**
     * 默认
     * 0 未知数据源
     */
    public static final int DEFAULT = 0;
    /**
     * 0 未知数据源
     */
    public static final int UNKNOWN = 0;


    /**
     * 数据供应商，1-雷速
     */
    public static final int RES_LEISU = 1;


    /**
     * 项目，1-足球
     */
    public static final String ITEM_FOOTBALL = "1";
    /**
     * 项目，2-篮球
     */
    public static final String ITEM_BASKETBALL = "2";


    /**
     * 1 雷速足球
     */
    public static final int LEISU_FOOTBALL = 1;
    /**
     * 2 雷速篮球
     */
    public static final int LEISU_BASKETBALL = 2;
    /**
     * 3 夸克足球
     */
    public static final int KUAKE_FOOTBALL = 3;
    /**
     * 4 夸克篮球
     */
    public static final int KUAKE_BASKETBALL = 4;
    /**
     * 100 自身数据
     */
    public static final int SELF = 100;

    private static final Map<Object, String> DICT_MAP = new HashMap<>();

    static {
        DICT_MAP.put(UNKNOWN, "未知数据源");
        DICT_MAP.put(LEISU_FOOTBALL, "雷速足球");
        DICT_MAP.put(LEISU_BASKETBALL, "雷速篮球");
//        DICT_MAP.put(KUAKE_FOOTBALL,"夸克足球");
//        DICT_MAP.put(KUAKE_BASKETBALL,"夸克篮球");
        DICT_MAP.put(SELF, "自身数据");
    }

    private static final List<Resource> ResourceIds = Lists.newArrayList();

    @Getter
    @AllArgsConstructor
    private static class Resource {
        private int resourceId;
        private int resId;
        private String itemId;
    }

    static {
        ResourceIds.add(new Resource(LEISU_FOOTBALL, RES_LEISU, ITEM_FOOTBALL));
        ResourceIds.add(new Resource(LEISU_BASKETBALL, RES_LEISU, ITEM_BASKETBALL));
    }

    public static String getItemId(int resourceId) {
        Resource resource = ResourceIds.stream()
                .filter(x -> x.getResourceId()==resourceId)
                .findFirst()
                .orElse(null);
        Assert.notNull(resource, "找不到对应的项目");

        return resource.getItemId();
    }

    public static int getResourceId(int resId, String itemId) {
        Resource resource = ResourceIds.stream()
                .filter(x -> x.getItemId().equals(itemId) && x.getResId()==resId)
                .findFirst()
                .orElse(null);
        Assert.notNull(resource, "找不到对应的数据源");

        return resource.getResourceId();
    }

    public static boolean isValid(Integer value) {
        if (value == null) {
            return false;
        }
        if (!DICT_MAP.containsKey(value)) {
            log.warn("来源类型未知，值为：{}", value);
            return false;
        }
        return true;
    }

    private ResSourceId() {

    }

    public static String getName(Integer value) {
        if (isValid(value)) {
            return DICT_MAP.get(value);
        }
        return DICT_MAP.get(DEFAULT);
    }

    public static List<DictItemDto> getList() {
        List<DictItemDto> dictItemDtoList = new ArrayList<>();
        for (Object key : DICT_MAP.keySet()) {
            String keyStr = key.toString();
            if ("100".equals(keyStr) || "0".equals(keyStr)) {
                continue;
            }
            DictItemDto dictItemDto = new DictItemDto();
            dictItemDto.setId(keyStr);
            dictItemDto.setName(DICT_MAP.get(key));
            dictItemDtoList.add(dictItemDto);
        }
        return dictItemDtoList;
    }
}
