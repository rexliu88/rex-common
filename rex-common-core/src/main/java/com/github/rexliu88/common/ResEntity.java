package com.github.rexliu88.common;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.lang.reflect.Field;
import java.util.Date;

/**
 * 资源实体接口
 *
 * 实体固定字段
 * 创建时间、创建人
 * 修改时间、修改人
 * 逻辑删除字段
 *
 * 关联 - 关系固定字段
 * id 等字段 在真正的实体类里面
 * 来源Id          采集方 id
 * 数据状态
 * 转换状态
 * 匹配状态
 * 关联id
 * 关联父id
 * 确认状态
 */
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
     * 1. 数据抓取，从业务系统 接口 或 数据库表 中抓取
     * 2. 数据转换，有 数据加工清洗 的意思，转换业务系统的字段值、枚举值，并设置无效数据，便于后续统一匹配写主表
     * 3. 关系匹配，匹配 业务系统主键 和 业务系统主键 的关系，以及 连带的 关系数据中的业务系统父Id
     * 4. 关系确认，确认关系关系数据，并设置确认状态，匹配成功已确认的数据 后续 统一写入主表
     * 5. 统一写入主表
     */
    // 来源id - 保留
    public static final String SOURCE_ID = "source_id";

    // 数据状态 ：0 抓取完成 1 转换完成 2 匹配完成 3 写入主库完成
    // 数据加工步骤
    // 分离
    public static final String DATA_STATUS = "data_status";

    // 转换状态 ：0  未转换 1  已转换
    // 分离到 批次关系过程表 中，进行转换
    public static final String TRANSFORM_STATUS = "transform_status";

    // 匹配状态 ：-1 匹配失败\关系解绑 0 未匹配 1 自动匹配成功 2 自动匹配失败需人工 3 人工手动匹配
    // 分离到 批次关系过程表 中，进行匹配
    public static final String MATCHED_STATUS = "matched_status";

    // 关联id    - 保留
    public static final String RELATION_ID = "relation_id";
    // 关联父id  -  保留
    public static final String RELATION_PARENT_ID = "relation_parent_id";

    // 确认状态 -1  未匹配上 0  未确认 1  已确认
    // 分离到 批次关系过程表 中，进行确认 ，分为自动确认，以及生成需要人工确认的任务项 -> 发送确认任务单，确认 数据生效时间 默认下个月1号
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
    // 关系 - 关联
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
