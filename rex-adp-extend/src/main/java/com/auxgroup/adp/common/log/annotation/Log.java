package com.auxgroup.adp.common.log.annotation;

import com.auxgroup.adp.common.log.enums.BusinessType;
import com.auxgroup.adp.common.log.enums.OperatorType;

import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Log {
    String client() default "";

    String title() default "";

    BusinessType businessType() default BusinessType.OTHER;

    OperatorType operatorType() default OperatorType.MANAGE;

    boolean isSaveRequestData() default true;

    boolean isSaveResponseData() default true;
}
