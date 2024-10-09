package com.auxdemo.adp.commons.group.utils;


import cn.hutool.core.util.StrUtil;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;

import com.auxdemo.adp.commons.group.constant.FeignConstants;

public class IdentityUtils {
    public static Long getUserId(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.USER_ID_KEY);
            return value==null?null:Long.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getCompanyCode(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.COMPANY_CODE_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getUserName(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.USERNAME_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getNickName(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.NICKNAME_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getMobile(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.USER_MOBILE_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getOaUsername(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.OA_USERNAME_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getBadgeNo(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.BADGE_NO_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getLoginFrom(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.LOGIN_FROM_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static Boolean getIsAdmin(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.USER_IS_ADMIN_KEY);
            return value==null?Boolean.FALSE:Boolean.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getGatewayClientId(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.GATEWAY_CLIENT_ID_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getLoginAppCode(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.LOGIN_APP_CODE_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getBrowserLanguage(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.BROWSER_LANGUAGE);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getBrowserLanguageNew() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            // 优先取请求头中的值
            String value = request.getHeader("Browser-Language");
            if (StrUtil.isNotBlank(value)) {
                return decode(value);
            }
            // 遍历请求头中的所有值，忽略大小写，找到最后一个匹配得的值
            Enumeration<String> headers = request.getHeaderNames();
            if (headers != null) {
                while(headers.hasMoreElements()) {
                    String headerKey = (String)headers.nextElement();
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
    public static Boolean getIsLoginUnion(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.LOGIN_APP_IS_UNION_KEY);
            return value==null?Boolean.FALSE:Boolean.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }
    public static String getRouterKey(){
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();
            String value = request.getHeader(FeignConstants.ROUTER_KEY);
            return decode(value);
        } catch (Exception e) {
            return null;
        }
    }

    private static String decode(String str){
        return decode(str,"UTF-8");
    }

    private static String decode(String str, String enc) {
        if (StrUtil.isEmpty(str)) {
            return str;
        }
        try {
            return URLDecoder.decode(str, enc);
        } catch (UnsupportedEncodingException e) {
            return str;
        }
    }
}
