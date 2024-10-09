package com.auxgroup.adp.commons.group.beans;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Data
public class AuxResponse<T>{
    private boolean success;
    private String errorCode;
    private String msg;
    private  T data;
    public static final String SUCCESS= "true";
    public static final String FAILED = "false";
    public static <T>AuxResponse <T>FAILED (){
        return  new AuxResponse<>(false);
    }

    public static <T> AuxResponse<T> FAILED(String msg) {
        return new AuxResponse<T>(false).msg(msg);
    }

    public static <T> AuxResponse<T> FAILED(String errorCode, String msg) {
        return new AuxResponse<T>(false).errorCode(errorCode).msg(msg);
    }

    public static <T> AuxResponse <T>SUCCESS (){
        return  new AuxResponse<>(true);
    }

    public static <T> AuxResponse<T> SUCCESS(T data) {
        return new AuxResponse<>(data, true);
    }
    public static boolean isSuccess(AuxResponse result){
        return result!=null && result.success;
    }

    public AuxResponse(T data, boolean success) {
        this.success = success;
        this.data = data;
    }
    private AuxResponse(Boolean success) {
        this.success = success;
    }

    public AuxResponse <T>data(T data){
        this.data = data;
        return this;
    }

    public AuxResponse<T> errorCode(String errorCode) {
        this.errorCode = errorCode;
        return this;
    }

    public AuxResponse<T> msg(String msg){
        this.msg = msg;
        return this;
    }

    public AuxResponse<T> msg(String msgPatten,Object ...param){
        List<String> paramNew = Arrays.asList(param).stream().map(a->a==null?"":a.toString()).collect(Collectors.toList());
        msgPatten = msgPatten.replaceAll("\\{\\}","%s");
        this.msg = String.format(msgPatten,paramNew.toArray(new String[paramNew.size()]));
        return this;
    }

    public AuxResponse log(){
        if(!StringUtils.isEmpty(this.msg)){
            log.info(this.msg);
        }
        return this;
    }
}
