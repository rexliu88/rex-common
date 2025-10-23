package com.auxdemo.adp.commons.utils;

import cn.hutool.core.util.StrUtil;

public class AuxStringUtils {

    public static String stripEnd(String str, String stripChars) {
        // 先检查主字符串是否为空
        if (StrUtil.isEmpty(str)) {
            return str;
        }

        // 如果stripChars为空或空白，则去除str末尾的所有空白字符
        if (StrUtil.isBlank(stripChars)) {
            return StrUtil.trimEnd(str);
        } else {
            // 否则去除str末尾指定的字符串
            return StrUtil.removeSuffix(str, stripChars);
        }
    }
}
