package com.auxdemo.adp.commons.group.excel.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CellMerge {
    int index() default -1;
}
