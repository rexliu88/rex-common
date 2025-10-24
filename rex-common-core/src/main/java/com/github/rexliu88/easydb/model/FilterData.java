package com.github.rexliu88.easydb.model;

import com.github.rexliu88.easydb.constant.FieldFilterType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.Set;

/**
 * 过滤字段数据
 */
@EqualsAndHashCode
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

    // 增加非空校验
    public FilterData(String columnName, Object columnValue) {
        this(columnName, columnValue, FieldFilterType.EQUAL_FILTER);
    }

    /**
     * 需要 将 fieldFilterType 转为枚举类型,这样 就不会重载调用异常了。
     * @param columnName
     * @param columnValue
     * @param fieldFilterType
     */
    public FilterData(String columnName, Object columnValue, int fieldFilterType) {
        validateColumnName(columnName);
        validateColumnValue(columnValue);
        this.filterType = fieldFilterType;
        this.columnName = columnName;
        this.columnValue = columnValue;
    }

    public FilterData(String columnName, Object columnValueStart, Object columnValueEnd) {
        validateColumnName(columnName);
        validateColumnValue(columnValueStart);
        validateColumnValue(columnValueEnd);
        this.filterType = FieldFilterType.RANGE_FILTER;
        this.columnName = columnName;
        this.columnValueStart = columnValueStart;
        this.columnValueEnd = columnValueEnd;
    }

    public FilterData(String columnName, Set<String> columnValueList) {
        validateColumnName(columnName);
        this.filterType = FieldFilterType.IN_LIST_FILTER;
        this.columnName = columnName;
        this.columnValueList = columnValueList;
    }

    private void validateColumnName(String columnName) {
        if (columnName == null || columnName.isEmpty()) {
            throw new IllegalArgumentException("Column name must not be null or empty.");
        }
    }

    private void validateColumnValue(Object columnValue) {
        if (columnValue == null) {
            throw new IllegalArgumentException("Column value cannot be null");
        }
    }
}
