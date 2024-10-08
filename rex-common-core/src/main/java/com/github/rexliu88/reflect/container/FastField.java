package com.github.rexliu88.reflect.container;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

/**
 * 字段 包装类
 * 字段本身
 * 字段注解
 */
public class FastField {
    private final Field field;
    private volatile Annotation[] attributes;

    public FastField(Field field) {
        this.field = field;
    }

    public Field getField() {
        return field;
    }

    public Annotation[] getAttributes() {
        if (attributes == null) {
            synchronized (this) {
                if (attributes == null) {
                    attributes = field.getAnnotations();
                }
            }
        }
        return attributes;
    }

}
