package com.auxdemo.adp.commons.group.excel.annotation;

import java.lang.annotation.*;

/**
 * Excel单元格合并注解
 * 用于标记需要合并的字段，指定合并的列索引
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CellMerge {
    /**
     * 合并列的索引位置
     * @return 列索引，默认值为-1表示未设置
     */
    int index() default -1;
}
