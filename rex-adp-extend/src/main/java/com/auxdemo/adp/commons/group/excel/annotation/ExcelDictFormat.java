package com.auxdemo.adp.commons.group.excel.annotation;

import java.lang.annotation.*;

/**
 * Excel字典格式注解类
 * <p>
 * 用于标记Excel导入导出时需要进行字典转换的字段，支持自定义读取转换表达式和分隔符
 * </p>
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelDictFormat {
    /**
     * 读取转换表达式
     * <p>
     * 用于定义字典值与显示值之间的映射关系，格式为"key1:value1,key2:value2"
     * </p>
     *
     * @return 转换表达式字符串，默认为空字符串
     */
    String readConverterExp() default "";

    /**
     * 分隔符
     * <p>
     * 用于分割多个字典项的分隔符
     * </p>
     *
     * @return 分隔符字符串，默认为逗号","
     */
    String separator() default ",";
}
