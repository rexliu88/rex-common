package com.auxdemo.adp.commons.group.utils;

import cn.hutool.extra.spring.SpringUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuxValidatorUtil {
    private static volatile Validator validator;

    private static Validator getValidator() {
        if (validator == null) {
            synchronized (AuxValidatorUtil.class) {
                if (validator == null) {
                    validator = SpringUtil.getBean(Validator.class);
                }
            }
        }
        return validator;
    }
    public static <T> void validate(T object, Class<?>... groups) {
        Set<ConstraintViolation<T>> validate = getValidator().validate(object, groups);
        if (!validate.isEmpty()) {
            throw new ConstraintViolationException("参数校验异常", validate);
        }
    }
}
