package com.github.rexliu88.service;

import cn.hutool.core.util.StrUtil;
import com.github.rexliu88.constant.CacheKey;
import com.github.rexliu88.constant.LeiSuResType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

@Data
@Slf4j
@Component
@NoArgsConstructor
public class CacheService implements Serializable {
    @Autowired
    StringRedisTemplate stringRedisTemplate;
    /**
     * redis key格式
     * res:sourceId:type:res_id
     */
    public static final String BaseKey = "res:%d:%s:%s";
    public String getRediskey(Integer sourceId, LeiSuResType leiSuResType, String resId){
        return String.format(BaseKey,sourceId, leiSuResType.name(),resId);
    }
    public boolean hasRedisCache(Integer sourceId, LeiSuResType leiSuResType, String resId) {
        if(stringRedisTemplate==null){
            //log.error("stringRedisTemplate 未初始化");
            return false;
        }
        if (!stringRedisTemplate.hasKey(getRediskey(sourceId, leiSuResType,resId))) {
            //log.error("资源 key : {} , redis key : {} 不存在", resId,getRediskey(sourceId, leiSuResType,resId));
            return false;
        } else {
            return true;
        }
    }
    public String getParamValue(Integer sourceId, LeiSuResType leiSuResType, String resId, String paramName){
        if(!hasRedisCache(sourceId, leiSuResType,resId)){
            return null;
        }
        if(hasRedisCache(sourceId, leiSuResType,resId)){
            String hValue = (String) stringRedisTemplate.opsForHash().get(getRediskey(sourceId, leiSuResType,resId), paramName);
            if (StrUtil.isAllNotBlank(hValue)) {
                return hValue;
            }
        }
        return null;
    }
    public void setParamValue(Integer sourceId, LeiSuResType leiSuResType, String resId, String paramName, String paramValue){
        if(hasRedisCache(sourceId, leiSuResType,resId)) {
            stringRedisTemplate.opsForHash().delete(getRediskey(sourceId, leiSuResType,resId), paramName);
        }
        stringRedisTemplate.opsForHash().putIfAbsent(getRediskey(sourceId, leiSuResType,resId), paramName, paramValue);
        if(leiSuResType == LeiSuResType.match) {
            setExpire(sourceId, leiSuResType,resId, 30, TimeUnit.DAYS); //比赛 暂时存储时间短一点 每天会抓 前后30天的比赛 所以时间可以短一点
        } else {
            setExpire(sourceId, leiSuResType,resId, 60, TimeUnit.DAYS);
        }
    }
    public void setExpire(Integer sourceId, LeiSuResType leiSuResType, String resId, Integer time, TimeUnit timeUnit){
        stringRedisTemplate.expire(getRediskey(sourceId, leiSuResType,resId), time, timeUnit);
    }
    public LeiSuResType getParentCacheType(Integer sourceId, LeiSuResType leiSuResType, String resId){
        String  cacheTypeName = getParamValue(sourceId, leiSuResType,resId, CacheKey.parentCacheType.name());
        if (StrUtil.isAllNotBlank(cacheTypeName)) {
            return LeiSuResType.valueOf(cacheTypeName);
        }
        return null;
    }
    public void setParentCacheType(Integer sourceId, LeiSuResType leiSuResType, String resId, LeiSuResType parentLeiSuResType){
        setParamValue(sourceId, leiSuResType,resId,CacheKey.parentCacheType.name(), parentLeiSuResType.name());
    }
    public String getName(Integer sourceId, LeiSuResType leiSuResType, String resId){
        String name = getParamValue(sourceId, leiSuResType,resId,CacheKey.name.name());
        if (StrUtil.isAllNotBlank(name)) {
            return name;
        }
        return "未知名称";
    }
    public void setName(Integer sourceId, LeiSuResType leiSuResType, String resId, String name){
        setParamValue(sourceId, leiSuResType,resId,CacheKey.name.name(),name);
    }
    public String getRelationId(Integer sourceId, LeiSuResType leiSuResType, String resId){
        String relationId = getParamValue(sourceId, leiSuResType,resId,CacheKey.relationId.name());
        if (StrUtil.isAllNotBlank(relationId)) {
            return relationId;
        }
        return null;
    }
    public void setRelationId(Integer sourceId, LeiSuResType leiSuResType, String resId, String relationId){
        setParamValue(sourceId, leiSuResType,resId,CacheKey.relationId.name(),relationId);
    }
    public String getParentId(Integer sourceId, LeiSuResType leiSuResType, String resId){
        String parentId = getParamValue(sourceId, leiSuResType,resId,CacheKey.parentId.name());
        if (StrUtil.isAllNotBlank(parentId)) {
            return parentId;
        }
        return null;
    }
    public void setParentId(Integer sourceId, LeiSuResType leiSuResType, String resId, String parentId){
        setParamValue(sourceId, leiSuResType,resId,CacheKey.parentId.name(),parentId);
    }

}
