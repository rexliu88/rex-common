package com.github.rexliu88.common;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表字段数据对象
 */
@Data
@NoArgsConstructor
public class ColumnData {
    /**
     * 字段名
     */
    private String columnName;

    /**
     * 字段值
     */
    private Object columnValue;

    public ColumnData(String columnName,Object columnValue){
        this.columnName = columnName;
        this.columnValue = columnValue;
    }
}