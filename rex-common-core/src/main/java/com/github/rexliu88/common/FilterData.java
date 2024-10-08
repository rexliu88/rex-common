package com.github.rexliu88.common;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 过滤字段数据
 */
@Data
@NoArgsConstructor
public class FilterData {
    /**
     * 过滤字段名
     */
    private String columnName;

    /**
     * 过滤值
     */
    private Object columnValue;

    /**
     * 范围比较的最小值
     */
    private Object columnValueStart;

    /**
     * 范围比较的最大值
     */
    private Object columnValueEnd;

    /**
     * 仅当操作符为IN的时候使用
     */
    private Set<String> columnValueList;

    /**
     * 过滤类型，参考FieldFilterType常量对象。缺省值就是等于过滤了
     */
    private Integer filterType = FieldFilterType.EQUAL_FILTER;

    public FilterData(String columnName,Object columnValue){
        this.filterType = FieldFilterType.EQUAL_FILTER;
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    public FilterData(String columnName,Object columnValue, int fieldFilterType){
        this.filterType = fieldFilterType;
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    public FilterData(String columnName, int fieldFilterType, Object columnValueStart, Object columnValueEnd){
        this.filterType = FieldFilterType.RANGE_FILTER;
        this.columnName = columnName;
        this.columnValueStart = columnValueStart;
        this.columnValueEnd = columnValueEnd;
    }

    public FilterData(String columnName, int fieldFilterType, Set<String> columnValueList){
        this.filterType = FieldFilterType.IN_LIST_FILTER;
        this.columnName = columnName;
        this.columnValueList = columnValueList;
    }
}
