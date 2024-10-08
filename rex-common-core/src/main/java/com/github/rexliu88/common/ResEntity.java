package com.github.rexliu88.common;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.lang.reflect.Field;
import java.util.Date;

public interface ResEntity  {
    /**
     * 实体固定字段
     */
    public static final String ADD_TIME = "add_time";
    public static final String ADD_USER = "add_user";
    public static final String UPDATE_TIME = "update_time";
    public static final String UPDATE_USER = "update_user";
    public static final String DELETED = "deleted";
    /**
     * 关系固定字段
     */
    public static final String SOURCE_ID = "source_id";
    public static final String DATA_STATUS = "data_status";
    public static final String TRANSFORM_STATUS = "transform_status";
    public static final String MATCHED_STATUS = "matched_status";
    public static final String RELATION_ID = "relation_id";
    public static final String RELATION_PARENT_ID = "relation_parent_id";
    public static final String CONFIRM_STATUS = "confirm_status";

    public static final String LogicFields = "add_time,add_user,update_time,update_user,deleted,source_id,data_status,transform_status,matched_status,relation_id,relation_parent_id,confirm_status";

    // 自带
    Date getAddTime();
    String getAddUser();
    Date getUpdateTime();
    String getUpdateUser();
    String getDeleted();

    void setAddTime(Date updateTime);
    void setAddUser(String updateUser);
    void setUpdateTime(Date updateTime);
    void setUpdateUser(String updateUser);
    void setDeleted(String deleted);
    // 关系
    Integer getSourceId();
    Integer getDataStatus();
    Integer getTransformStatus();
    Integer getMatchedStatus();
    String getRelationId();
    String getRelationParentId();
    Integer getConfirmStatus();

    void setSourceId(Integer sourceId);
    void setDataStatus(Integer dataStatus);
    void setMatchedStatus(Integer matchedStatus);
    void setRelationId(String relationId);
    void setRelationParentId(String relationParentId);
    void setConfirmStatus(Integer confirmStatus);

    // 增强
    default String getPrimaryKeyColumnName(){
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            boolean hasTableId = field.isAnnotationPresent(TableId.class);
            if(hasTableId){
                TableId tableId = field.getAnnotation(TableId.class);
                return tableId.value();
            }
        }
        throw new RuntimeException("实体未设置主键字段");
    }
    default String getPrimaryKeyColumnValue(){
        for (Field field : this.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            boolean hasTableId = field.isAnnotationPresent(TableId.class);
            if(hasTableId){
                String tableIdValue = null;
                try {
                    tableIdValue = (String)field.get(this);//得到此属性的值
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
                if(StrUtil.isAllNotBlank(tableIdValue)) {
                    return tableIdValue;
                } else {
                    throw new RuntimeException("实体未设置主键字段值");
                }
            }
        }
        throw new RuntimeException("实体未设置主键字段");
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
