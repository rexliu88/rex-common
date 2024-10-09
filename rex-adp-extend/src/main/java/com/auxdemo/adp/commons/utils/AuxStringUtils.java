package com.auxdemo.adp.commons.utils;

import cn.hutool.core.util.StrUtil;

public class AuxStringUtils {

    public static String stripEnd(String str, String stripChars) {
        if (StrUtil.isBlank(stripChars)) {
            return StrUtil.trimEnd(str);
        } else {
            return StrUtil.removeSuffix(str, stripChars);
        }
    }
}
