package com.github.rexliu88.dao;

import com.github.rexliu88.common.ColumnData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResEntityDao {
    /**
     * 用主键字段获取一个实体数据
     * @param tableName 表名称
     * @param selectFieldList 返回字段列表
     * @param primaryKeyColumnDataList 主键过滤字段列表,不能为空 必须带主键来查询
     * @param dataPermFilter 数据权限过滤字符串
     * @return
     */
    @Select("<script>"
            + "SELECT "
            + "    <foreach collection=\"selectFieldList\" item=\"columnName\" separator=\",\">"
            + "       ${columnName} "
            + "    </foreach>"
            + " FROM ${tableName} "
            + "<where>"
            + "    <foreach collection=\"primaryKeyColumnDataList\" item=\"columnData\" >"
            + "        AND ${columnData.columnName} = #{columnData.columnValue} "
            + "    </foreach>"
            + "    <if test=\"dataPermFilter != null and dataPermFilter != ''\">"
            + "        AND ${dataPermFilter} "
            + "    </if>"
            + "</where>"
            + " limit 0,1 "
            + "</script>")
    Map<String, Object> getOneByPrimaryKey(@Param("tableName") String tableName,
                                           @Param("selectFieldList") List<String> selectFieldList,
                                           @Param("primaryKeyColumnDataList") List<ColumnData> primaryKeyColumnDataList,
                                           @Param("dataPermFilter") String dataPermFilter);

    /**
     * 获取实体的字段名列表
     * @param tableName 表名称
     * @return 字段名列表
     */
    @Select("<script>"
            + "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE "
            + " table_name = #{tableName} "
            + "</script>")
    List<String> getFieldListByTableName(@Param("tableName") String tableName);

    @Select("<script>"
            + "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE "
            + " table_name = #{tableName} "
            + " and COLUMN_NAME in ('add_time','add_user','update_time','update_user','deleted','source_id','data_status','transform_status','matched_status','relation_id','relation_parent_id','confirm_status') "
            + "</script>")
    List<String> getLogicFieldListByTableName(@Param("tableName") String tableName);

    /**
     * 获取实体的主键字段名列表
     * @param tableName 表名称
     * @return 字段名列表
     */
    @Select("<script>"
            + "SELECT COLUMN_NAME FROM information_schema.statistics WHERE "
            + " table_name = #{tableName} and index_name='uk_code' "
            + "</script>")
    List<String> getPrimaryKeyFieldListByTableName(@Param("tableName") String tableName);

    @Select("<script>"
            + "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE "
            + " table_name = #{tableName} "
            + " and COLUMN_NAME != 'id' "
            + " and COLUMN_NAME not in ('add_time','add_user','update_time','update_user','deleted','source_id','data_status','relation_id','relation_parent_id') "
            + " and COLUMN_NAME not in ("
            + "SELECT COLUMN_NAME FROM information_schema.statistics WHERE "
            + " table_name = #{tableName} and index_name='uk_code' "
            + ") "
            + "</script>")
    List<String> getFetchDataFieldListByTableName(@Param("tableName") String tableName);

    @Select("<script>"
            + "SELECT COLUMN_NAME FROM information_schema.COLUMNS WHERE "
            + " table_name = #{tableName} "
            + " and COLUMN_NAME like 'transform_%' "
            + "</script>")
    List<String> getTransformDataFieldListByTableName(@Param("tableName") String tableName);

    @Select("<script>"
            + "SELECT COLUMN_NAME FROM information_schema.statistics WHERE "
            + " table_name = #{tableName} and index_name='uk_code' "
            + " and COLUMN_NAME like '%_id' and COLUMN_NAME != 'source_id' "
            + " limit 0,1 "
            + "</script>")
    String getPrimaryKeyFieldNameByTableName(@Param("tableName") String tableName);
}
