package com.github.rexliu88.dao;

import com.github.rexliu88.common.FilterData;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

@Mapper
public interface ResGetKeyValueListDao {
    @Select("<script>"
            + "SELECT "
            + " ${keyColumnName}, "
            + " ${valueColumnName} "
            + " FROM ${tableName} "
            + "<where>"
            + "    <if test=\"filterDataList != null\">"
            + "        <foreach collection=\"filterDataList\" item=\"filterData\">"
            + "            <if test=\"filterData.filterType == 1\">"
            + "                AND ${filterData.columnName} = #{filterData.columnValue} "
            + "            </if>"
            + "            <if test=\"filterData.filterType == 2\">"
            + "                <choose>"
            + "                    <when test=\"filterData.columnValueStart != null and filterData.columnValueEnd != null\">"
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
            + "    <if test=\"orFilterDataList != null\">"
            + "        AND "
            + "        <foreach collection=\"orFilterDataList\" item=\"filterData\" index=\"index\" open=\"(\" close=\")\">"
            + "            <if test=\"index != 0\">"
            + "            OR "
            + "            </if>"
            + "            <if test=\"filterData.filterType == 1\">"
            + "                ${filterData.columnName} = #{filterData.columnValue} "
            + "            </if>"
            + "            <if test=\"filterData.filterType == 2\">"
            + "                <choose>"
            + "                    <when test=\"filterData.columnValueStart != null and filterData.columnValueEnd != null\">"
            + "                        ( ${filterData.columnName} &gt;= #{filterData.columnValueStart} AND ${filterData.columnName} &lt;= #{filterData.columnValueEnd} ) "
            + "                    </when>"
            + "                    <otherwise>"
            + "                        <if test=\"filterData.columnValueStart != null\">"
            + "                            ${filterData.columnName} &gt;= #{filterData.columnValueStart} "
            + "                        </if>"
            + "                        <if test=\"filterData.columnValueEnd != null\">"
            + "                            ${filterData.columnName} &lt;= #{filterData.columnValueEnd} "
            + "                        </if>"
            + "                    </otherwise>"
            + "                </choose>"
            + "            </if>"
            + "            <if test=\"filterData.filterType == 3\">"
            + "                <bind name = \"safeColumnValue\" value = \"'%' + filterData.columnValue + '%'\" />"
            + "                ${filterData.columnName} LIKE #{safeColumnValue} "
            + "            </if>"
            + "            <if test=\"filterData.filterType == 4\">"
            + "                ${filterData.columnName} IN "
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
    List<Map<String, Object>> getDictDataList(
            @Param("tableName") String tableName,
            @Param("keyColumnName") String keyColumnName,
            @Param("valueColumnName") String valueColumnName,
            @Param("filterDataList") List<FilterData> filterDataList,
            @Param("orFilterDataList") List<FilterData> orFilterDataList,
            @Param("dataPermFilter") String dataPermFilter);
}
