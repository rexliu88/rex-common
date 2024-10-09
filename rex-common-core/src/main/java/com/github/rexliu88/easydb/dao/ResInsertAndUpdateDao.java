package com.github.rexliu88.easydb.dao;

import com.github.rexliu88.easydb.model.ColumnData;
import com.github.rexliu88.easydb.model.FilterData;
import com.github.rexliu88.easydb.model.TableData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ResInsertAndUpdateDao {
    /**
     * 批量新增
     * @param tableDataList 表数据对象列表
     */
    @Insert("<script>"
            + "<foreach collection=\"tableDataList\" item=\"tableData\" separator=\";\">"
            + "INSERT INTO ${tableData.tableName} "
            + "    <foreach collection=\"tableData.columnDataList\" item=\"columnData\" separator=\",\" open=\"(\" close=\")\">"
            + "       ${columnData.columnName} "
            + "    </foreach>"
            + " VALUES "
            + "    <foreach collection=\"tableData.columnDataList\" item=\"columnData\" separator=\",\" open=\"(\" close=\")\">"
            + "       #{columnData.columnValue} "
            + "    </foreach>"
            + " ON DUPLICATE KEY UPDATE "
            + "    <foreach collection=\"tableData.columnDataList\" item=\"columnData\" separator=\",\" >"
            + "        <if test=\"columnData.columnName != null and columnData.columnName != 'deleted' and columnData.columnName != 'add_user' and columnData.columnName != 'add_time' \">"
            + "            <if test=\"columnData.columnValue != null\">"
            + "                ${columnData.columnName} = #{columnData.columnValue} "
            + "            </if>"
            + "            <if test=\"columnData.columnValue == null\">"
            + "                ${columnData.columnName} = NULL "
            + "            </if>"
            + "        </if>"
            + "    </foreach> "
            + "</foreach>"
            + "</script>")
    int batchInsertOrUpdate(@Param("tableDataList") List<TableData> tableDataList);

    @Update("<script>"
            + "<foreach collection=\"tableDataList\" item=\"tableData\" separator=\";\">"
            + "UPDATE ${tableData.tableName} SET "
            + "    <foreach collection=\"tableData.columnDataList\" item=\"columnData\" separator=\",\" >"
            + "        <if test=\"columnData.columnValue != null\">"
            + "            ${columnData.columnName} = #{columnData.columnValue} "
            + "        </if>"
            + "        <if test=\"columnData.columnValue == null\">"
            + "            ${columnData.columnName} = NULL "
            + "        </if>"
            + "    </foreach>"
            + "<where>"
            + "    <foreach collection=\"tableData.primaryKeyColumnDataList\" item=\"columnData\" >"
            + "        AND ${columnData.columnName} = #{columnData.columnValue} "
            + "    </foreach>"
            + "    <if test=\"dataPermFilter != null and dataPermFilter != ''\">"
            + "        AND ${dataPermFilter} "
            + "    </if>"
            + "</where>"
            + "</foreach>"
            + "</script>")
    int batchUpdate(
            @Param("tableDataList") List<TableData> tableDataList,
            @Param("dataPermFilter") String dataPermFilter);

    /**
     * 更新表数据。
     *
     */
    @Update("<script>"
            + "UPDATE ${tableName} SET "
            + "    <foreach collection=\"updateColumnList\" item=\"columnData\" separator=\",\" >"
            + "        <if test=\"columnData.columnValue != null\">"
            + "            ${columnData.columnName} = #{columnData.columnValue} "
            + "        </if>"
            + "        <if test=\"columnData.columnValue == null\">"
            + "            ${columnData.columnName} = NULL "
            + "        </if>"
            + "    </foreach>"
            + "<where>"
            + "    <if test=\"filterDataList != null\">"
            + "        <foreach collection=\"filterDataList\" item=\"filterData\">"
            + "            <if test=\"filterData.filterType == 1\">"
            + "                AND ${filterData.columnName} = #{filterData.columnValue} "
            + "            </if>"
            + "            <if test=\"filterData.filterType == 2\">"
            + "                <choose>"
            + "                    <when test=\"filterData.columnValueStart != null and filterData.columnValueEnd != null \">"
            + "                        AND ( ${filterData.columnName} &gt;= #{filterData.columnValueStart} AND ${filterData.columnName} &lt;= #{filterData.columnValueEnd} ) "
            + "                    </when>"
            + "                    <otherwise>"
            + "                        <if test=\"filterData.columnValueStart != null\">"
            + "                            AND ${filterData.columnName} &gt;= #{filterData.columnValueStart} "
            + "                        </if>"
            + "                        <if test=\"filterData.columnValueEnd != null\">"
            + "                            AND ${filterData.columnName} &lt;= #{filterData.columnValueEnd} "
            + "                        </if>"
            + "                    </otherwise>"
            + "                </choose>"
            + "            </if>"
            + "            <if test=\"filterData.filterType == 3\">"
            + "                <bind name = \"safeColumnValue\" value = \"'%' + filterData.columnValue + '%'\" />"
            + "                AND ${filterData.columnName} LIKE #{safeColumnValue} "
            + "            </if>"
            + "            <if test=\"filterData.filterType == 4\">"
            + "                AND ${filterData.columnName} IN "
            + "                <foreach collection=\"filterData.columnValueList\" item=\"columnValue\" separator=\",\" open=\"(\" close=\")\">"
            + "                    #{columnValue} "
            + "                </foreach>"
            + "            </if>"
            + "            <if test=\"filterData.filterType == 5\">"
            + "                AND ${filterData.columnName} != #{filterData.columnValue} "
            + "            </if>"
            + "        </foreach>"
            + "    </if>"
            + "    <if test=\"dataPermFilter != null and dataPermFilter != ''\">"
            + "        AND ${dataPermFilter} "
            + "    </if>"
            + "</where>"
            + "</script>")
    int update(
            @Param("tableName") String tableName,
            @Param("updateColumnList") List<ColumnData> updateColumnList,
            @Param("filterDataList") List<FilterData> filterDataList,
            @Param("dataPermFilter") String dataPermFilter);
}
