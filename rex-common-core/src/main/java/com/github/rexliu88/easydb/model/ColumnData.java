package com.github.rexliu88.easydb.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表字段数据对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnData {
    /**
     * 字段名
     */
    private String columnName;

    /**
     * 字段值
     */
    private Object columnValue;
}