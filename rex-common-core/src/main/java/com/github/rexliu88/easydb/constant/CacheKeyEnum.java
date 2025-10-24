package com.github.rexliu88.easydb.constant;

import com.auxgroup.adp.commons.group.config.enums.AuxEnum;
import com.auxgroup.adp.commons.utils.AuxEnumUtil;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 缓存key枚举
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CacheKeyEnum implements AuxEnum {
    TABLE("table:", "7200"),
    Entity("entity:", "实体"),
    OTHER("other", "其他");

    private String code;
    private String name;

    CacheKeyEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    @JsonCreator
    public static final CacheKeyEnum getByCode(String code) {
        CacheKeyEnum enumObject = AuxEnumUtil.parse(CacheKeyEnum.class, code);
        return enumObject;
    }

    public static final String getNameByCode(String code) {
        CacheKeyEnum enumObject = AuxEnumUtil.parse(CacheKeyEnum.class, code);
        if (enumObject != null) {
            return enumObject.getName();
        }
        return null;
    }
}
