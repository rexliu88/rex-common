package com.auxgroup.adp.commons.group.beans;

import com.auxdemo.adp.commons.group.utils.IdentityUtils;
import org.springframework.beans.factory.annotation.Value;

public class AuxBaseController {

    @Value("${spring.aux.appCode:''}")
    private String appCode;

    @Value("${spring.application.name:''}")
    private String applicationName;

    public Long getUserId(){
        return IdentityUtils.getUserId();
    }

    public String getUsername(){
        return IdentityUtils.getUserName();
    }

    public String getName(){
        return IdentityUtils.getNickName();
    }

    public String getMobile(){
        return IdentityUtils.getMobile();
    }

    public String getBadgeNo(){
        return IdentityUtils.getBadgeNo();
    }

    public String getOaUsername(){
        return IdentityUtils.getOaUsername();
    }

    public String getLoginFrom(){
        return IdentityUtils.getLoginFrom();
    }

    public String getTenantCode() {
        return IdentityUtils.getCompanyCode();
    }

    public Boolean getIsAdmin(){
        return IdentityUtils.getIsAdmin();
    }

    public String getGatewayClientId() {
        return IdentityUtils.getGatewayClientId();
    }

    public String getSettingAppCode() {
        return appCode!=null && appCode.length()>0?appCode:null;
    }

    public String getLoginAppCode(){
        return IdentityUtils.getLoginAppCode();
    }

    public Boolean getIsLoginUnion(){
        return IdentityUtils.getIsLoginUnion();
    }

    public String getSettingApplicationName(){
        return applicationName!=null && applicationName.length()>0?applicationName:null;
    }

    public String getBrowserLanguage(){
        return IdentityUtils.getBrowserLanguage();
    }
}
