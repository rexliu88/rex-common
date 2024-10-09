package com.auxgroup.adp.commons.utils;

import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AuxEnumUtil {
    private static final Logger log = LoggerFactory.getLogger(AuxEnumUtil.class);

    public static <T> T parse(Class<T> enumType, Object code) {
        return parse(enumType, code, "code");
    }
    public static <T> T parse(Class<T> enumType, Object code, String props) {
        if (code == null) {
            return null;
        } else {
            try {
                T[] values = enumType.getEnumConstants();
                PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();

                for(int i = 0; i < values.length; ++i) {
                    T v = (T)values[i];
                    Object value = propertyUtilsBean.getNestedProperty(v, props);
                    if (value != null && value.equals(code)) {
                        return v;
                    }
                }

                return null;
            } catch (Exception var10) {
                log.error("枚举转换异常", var10);
                return null;
            }
        }
    }

    private AuxEnumUtil() {
    }
}
