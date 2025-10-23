package com.auxdemo.adp.commons.group.utils;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.ServletUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;

import com.auxdemo.adp.commons.group.constant.FeignConstants;

public class IdentityUtils {
    private static HttpServletRequest getHttpServletRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes != null ? attributes.getRequest() : null;
        return request;
    }

    private static String getHeaderValue(String headerKey) {
        HttpServletRequest request = getHttpServletRequest();
        if (request == null) {
            return null;
        }
        String value = ServletUtil.getHeader(request, FeignConstants.USER_ID_KEY, StandardCharsets.UTF_8.toString());
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value;
    }

    public static Long getUserId() {
        try {
            String value = getHeaderValue(FeignConstants.USER_ID_KEY);
            return Convert.toLong(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getCompanyCode() {
        try {
            String value = getHeaderValue(FeignConstants.COMPANY_CODE_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getUserName() {
        try {
            String value = getHeaderValue(FeignConstants.USERNAME_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getNickName() {
        try {
            String value = getHeaderValue(FeignConstants.NICKNAME_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getMobile() {
        try {
            String value = getHeaderValue(FeignConstants.USER_MOBILE_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getOaUsername() {
        try {
            String value = getHeaderValue(FeignConstants.OA_USERNAME_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getBadgeNo() {
        try {
            String value = getHeaderValue(FeignConstants.BADGE_NO_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getLoginFrom() {
        try {
            String value = getHeaderValue(FeignConstants.LOGIN_FROM_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean getIsAdmin() {
        try {
            String value = getHeaderValue(FeignConstants.USER_IS_ADMIN_KEY);
            return value == null ? Boolean.FALSE : Boolean.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getGatewayClientId() {
        try {
            String value = getHeaderValue(FeignConstants.GATEWAY_CLIENT_ID_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getLoginAppCode() {
        try {
            String value = getHeaderValue(FeignConstants.LOGIN_APP_CODE_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getBrowserLanguage() {
        try {
            String value = getHeaderValue(FeignConstants.BROWSER_LANGUAGE);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getBrowserLanguageNew() {
        try {
            HttpServletRequest request = getHttpServletRequest();
            // 优先取请求头中的值
            String value = getHeaderValue("Browser-Language");
            if (StrUtil.isNotBlank(value)) {
                return decode(value);
            }
            // 遍历请求头中的所有值，忽略大小写，找到最后一个匹配得的值
            Enumeration<String> headers = request.getHeaderNames();
            if (headers != null) {
                while (headers.hasMoreElements()) {
                    String headerKey = (String) headers.nextElement();
                    if (StrUtil.equalsIgnoreCase(headerKey, "browser-language")) {
                        value = request.getHeader(headerKey);
                    }
                }
            }
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean getIsLoginUnion() {
        try {
            String value = getHeaderValue(FeignConstants.LOGIN_APP_IS_UNION_KEY);
            return value == null ? Boolean.FALSE : Boolean.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    public static String getRouterKey() {
        try {
            String value = getHeaderValue(FeignConstants.ROUTER_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    private static String decode(String str) {
        return decode(str, StandardCharsets.UTF_8.toString());
    }

    private static String decode(String str, String enc) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        try {
            return URLDecoder.decode(str, enc);
        } catch (UnsupportedEncodingException e) {
            // 可以考虑记录日志
            return str;
        }
    }
}
