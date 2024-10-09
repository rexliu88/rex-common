package com.auxdemo.adp.commons.group.excel.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ExcelDictFormat {
    String readConverterExp() default "";

    String separator() default ",";
}
