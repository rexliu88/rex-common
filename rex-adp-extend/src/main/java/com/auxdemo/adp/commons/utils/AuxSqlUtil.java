package com.auxdemo.adp.commons.utils;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;

public class AuxSqlUtil {
    public static final String SQL_REGEX = "select |insert |delete |update |drop |count |exec |chr |mid |master |truncate |char |and |declare ";
    public static final String SQL_PATTERN = "[a-zA-Z0-9_\\ \\,\\.]+";

    public static String escapeOrderBySql(String value) {
        if (StrUtil.isNotEmpty(value) && !isValidOrderBySql(value)) {
            throw new UtilException("参数不符合规范，不能进行查询");
        } else {
            return value;
        }
    }

    public static boolean isValidOrderBySql(String value) {
        return ReUtil.isMatch(SQL_PATTERN, value);
    }

    public static void filterKeyword(String value) {
        if (!StrUtil.isEmpty(value)) {
            String[] sqlKeywords = SQL_REGEX.split("\\|");
            String[] var2 = sqlKeywords;
            int var3 = sqlKeywords.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String sqlKeyword = var2[var4];
                if (StrUtil.indexOfIgnoreCase(value, sqlKeyword) > -1) {
                    throw new UtilException("参数存在SQL注入风险");
                }
            }

        }
    }
}
