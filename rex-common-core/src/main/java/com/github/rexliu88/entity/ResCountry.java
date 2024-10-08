package com.github.rexliu88.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.rexliu88.common.ResEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * 国家表
 *
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("res_country")
public class ResCountry implements ResEntity,Serializable {
    /**
     * 主键，自增
     */
    private Integer id;

    /**
     * 编码，唯一键
     */
    @TableId(value = "country_id", type = IdType.INPUT)
    private String countryId;

    /**
     * 国家编码
     */
    @TableField("country_code")
    private String countryCode;

    /**
     * 所属大洲
     */
    @TableField("continent")
    private String continent;

    /**
     * 所属大洲名称
     */
    @TableField("continent_name")
    private String continentName;

    /**
     * 国徽图片地址
     */
    @TableField("emblem_url")
    private String emblemUrl;

    /**
     * 国旗图片地址
     */
    @TableField("flag_url")
    private String flagUrl;

    /**
     * 中文名称
     */
    @TableField("country_name_cn")
    private String countryNameCn;

    /**
     * 中文简称
     */
    @TableField("country_name_en")
    private String countryNameEn;

    /**
     * 英文名称
     */
    @TableField("country_simple_name_cn")
    private String countrySimpleNameCn;

    /**
     * 英文简称
     */
    @TableField("country_simple_name_en")
    private String countrySimpleNameEn;

    /**
     * 数据创建时间
     */
    @TableField("add_time")
    private Date addTime;

    /**
     * 数据创建人员
     */
    @TableField("add_user")
    private String addUser;

    /**
     * 数据更新时间
     */
    @TableField("update_time")
    private Date updateTime;

    /**
     * 数据更新人员
     */
    @TableField("update_user")
    private String updateUser;

    /**
     * 逻辑删除
     */
    @TableField("deleted")
    private String deleted;

    /**
     * 来源Id
     */
    @TableField("source_id")
    private Integer sourceId;

    /**
     * 数据状态
     */
    @TableField("data_status")
    private Integer dataStatus;

    /**
     * 转换状态
     */
    @TableField("transform_status")
    private Integer transformStatus;

    /**
     * 匹配状态
     */
    @TableField("matched_status")
    private Integer matchedStatus;

    /**
     * 关联编码
     */
    @TableField("relation_id")
    private String relationId;

    /**
     * 关联父编码
     */
    @TableField("relation_parent_id")
    private String relationParentId;

    /**
     * 确认状态
     */
    @TableField("confirm_status")
    private Integer confirmStatus;

    /**
     * 转换国旗图片地址
     */
    @TableField("transform_flag_url")
    private String transformFlagUrl;

    /**
     * api数据更新时间
     */
    @TableField("api_updated_at")
    private Long apiUpdatedAt;


    public static final String ID = "id";

    public static final String COUNTRY_ID = "country_id";

    public static final String COUNTRY_CODE = "country_code";

    public static final String CONTINENT = "continent";

    public static final String CONTINENT_NAME = "continent_name";

    public static final String EMBLEM_URL = "emblem_url";

    public static final String FLAG_URL = "flag_url";

    public static final String COUNTRY_NAME_CN = "country_name_cn";

    public static final String COUNTRY_NAME_EN = "country_name_en";

    public static final String COUNTRY_SIMPLE_NAME_CN = "country_simple_name_cn";

    public static final String COUNTRY_SIMPLE_NAME_EN = "country_simple_name_en";

    public static final String ADD_TIME = "add_time";

    public static final String ADD_USER = "add_user";

    public static final String UPDATE_TIME = "update_time";

    public static final String UPDATE_USER = "update_user";

    public static final String DELETED = "deleted";

    public static final String SOURCE_ID = "source_id";

    public static final String DATA_STATUS = "data_status";

    public static final String TRANSFORM_STATUS = "transform_status";

    public static final String MATCHED_STATUS = "matched_status";

    public static final String RELATION_ID = "relation_id";

    public static final String RELATION_PARENT_ID = "relation_parent_id";

    public static final String CONFIRM_STATUS = "confirm_status";

    public static final String TRANSFORM_FLAG_URL = "transform_flag_url";

    public static final String API_UPDATED_AT = "api_updated_at";

    public static final String TABLE_NAME = "res_country";
}
