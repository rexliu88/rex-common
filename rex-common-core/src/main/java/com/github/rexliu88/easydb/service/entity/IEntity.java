package com.github.rexliu88.easydb.service.entity;

import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.Objects;

public interface IEntity {
    /**
     * 实体固定字段
     */
    public static final String createTime = "create_time";
    public static final String createBy = "create_by";
    public static final String updateTime = "update_time";
    public static final String updateBy = "update_by";
    public static final String delFlag = "del_flag";

    // 自带
    Date getCreateTime();
    String getCreateBy();
    Date getUpdateTime();
    String getUpdateBy();
    Boolean getDelFlag();

    void setCreateTime(Date createTime);
    void setCreateBy(String createBy);
    void setUpdateTime(Date updateTime);
    void setUpdateBy(String updateBy);
    void setDelFlag(Boolean delFlag);

    // 增强
    default Field getTableIdField(){
        for (Field field : ReflectUtil.getFields(this.getClass())) {
            field.setAccessible(true);
            boolean hasTableId = field.isAnnotationPresent(TableId.class);
            if(hasTableId){
                return field;
            }
        }
        throw new RuntimeException("实体未设置主键字段");
    }
    default String getTableIdColumnName(){
        Field field = getTableIdField();
        if(Objects.nonNull(field)){
            TableId tableId = field.getAnnotation(TableId.class);
            return tableId.value();
        }
        throw new RuntimeException("实体未设置主键字段");
    }
    default String getTableIdColumnValue(){
        Field field = getTableIdField();
        String tableIdValue = null;
        if(Objects.nonNull(field)){
            try {
                tableIdValue = (String)field.get(this);//得到此属性的值
            } catch (IllegalAccessException e) {
                throw new RuntimeException("实体获取主键字段值错误",e);
            }
            if(StrUtil.isNotBlank(tableIdValue)) {
                return tableIdValue;
            }
        }
        throw new RuntimeException("实体未设置主键字段值");
    }
    default String getTableName(){
        boolean hasTableName = this.getClass().isAnnotationPresent(TableName.class);
        if(hasTableName){
            TableName tableName = this.getClass().getAnnotation(TableName.class);
            return tableName.value();
        }
        return StrUtil.toUnderlineCase(this.getClass().getName());
    }
}
