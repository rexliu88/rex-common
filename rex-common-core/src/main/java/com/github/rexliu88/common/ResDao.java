package com.github.rexliu88.common;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.github.rexliu88.easydb.dao.*;
import com.github.rexliu88.dto.PageDto;
import com.github.rexliu88.easydb.model.ColumnData;
import com.github.rexliu88.easydb.model.FilterData;
import com.github.rexliu88.easydb.model.TableData;
import com.github.rexliu88.easydb.utils.LocalCache;
import com.github.rexliu88.reflect.FastReflect;
import com.github.rexliu88.reflect.container.FastField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ResDao基类
 */
@Component
@Slf4j
public class ResDao {
    /**
     * JOB系统用户
     */
    public User systemUser = new User("system", "system");
    /**
     * 实体Dao
     */
    @Autowired
    ResEntityDao resEntityDao;
    /**
     * 新增和更新Dao
     */
    @Autowired
    ResInsertAndUpdateDao resInsertAndUpdateDao;
    /**
     * 查询ListDao
     */
    @Autowired
    ResGetListDao resGetListDao;
    /**
     * 查询列表数量Dao
     */
    @Autowired
    ResGetListCountDao resGetListCountDao;
    /**
     * 查询key value字段Dao
     */
    @Autowired
    ResGetKeyValueListDao resGetKeyValueListDao;

    public <T> T getOneByPrimaryKey(String tableName, String primaryKeyColumnName, Object primaryKeyColumnValue, String dataPermFilter, Class<T> clazz) {
        List<ColumnData> primaryKeyColumnDataList = new ArrayList<>();
        ColumnData primaryKeyColumnData = new ColumnData(primaryKeyColumnName, primaryKeyColumnValue);
        primaryKeyColumnDataList.add(primaryKeyColumnData);
        return getOneByPrimaryKey(tableName, primaryKeyColumnDataList, dataPermFilter, clazz);
    }

    public <T> T getOneByPrimaryKey(String tableName, List<ColumnData> primaryKeyColumnDataList, String dataPermFilter, Class<T> clazz) {
        List<String> selectFieldList = resEntityDao.getFieldListByTableName(tableName);
        log.info("ResDao getOneByPrimaryKey tableName: {} , primaryKeyColumn :{} , dataPermFilter : {} , selectFieldList : {}", tableName, JSON.toJSONString(primaryKeyColumnDataList), dataPermFilter, JSON.toJSONString(selectFieldList));
        Map<String, Object> resultMap = resEntityDao.getOneByPrimaryKey(tableName, selectFieldList, primaryKeyColumnDataList, dataPermFilter);
        return BeanUtil.toBeanIgnoreError(resultMap, clazz);
    }

    public Integer getListCount(String tableName, List<FilterData> filterDataList, String dataPermFilter) {
        return getListCount(tableName, filterDataList, null, dataPermFilter);
    }

    public Integer getListCount(String tableName, List<FilterData> filterDataList, List<FilterData> orFilterDataList, String dataPermFilter) {
        log.info("ResDao getListCount tableName: {} , filterDataList :{} , orFilterDataList : {} , dataPermFilter : {}", tableName, JSON.toJSONString(filterDataList), JSON.toJSONString(orFilterDataList), dataPermFilter);
        return resGetListCountDao.getListCount(tableName, filterDataList, orFilterDataList, dataPermFilter);
    }

    public <T> List<T> getList(String tableName, List<FilterData> filterDataList, String dataPermFilter, String orderBy, Integer pageNo, Integer pageSize, Class<T> clazz) {
        return getList(tableName, filterDataList, null, dataPermFilter, orderBy, pageNo, pageSize, clazz);
    }

    public <T> List<T> getList(String tableName, List<FilterData> filterDataList, List<FilterData> orFilterDataList, String dataPermFilter, String orderBy, Integer pageNo, Integer pageSize, Class<T> clazz) {
        List<String> selectFieldList = resEntityDao.getFieldListByTableName(tableName);
        return getList(tableName, selectFieldList, filterDataList, orFilterDataList, dataPermFilter, orderBy, pageNo, pageSize, clazz);
    }

    public <T> List<T> getList(String tableName, List<String> selectFieldList, List<FilterData> filterDataList, List<FilterData> orFilterDataList, String dataPermFilter, String orderBy, Integer pageNo, Integer pageSize, Class<T> clazz) {
        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize != null && pageSize > 1000) {
            pageSize = 1000;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 50;
        }
        log.info("ResDao getList tableName: {} , filterDataList :{} , orFilterDataList : {} , dataPermFilter : {},orderBy : {} ,pageNo : {} ,pageSize : {}, selectFieldList : {}", tableName, JSON.toJSONString(filterDataList), JSON.toJSONString(orFilterDataList), dataPermFilter, orderBy, pageNo, pageSize, JSON.toJSONString(selectFieldList));
        List<Map<String, Object>> resultMapList = resGetListDao.getList(tableName, selectFieldList, filterDataList, orFilterDataList, dataPermFilter, orderBy, pageNo, pageSize);
        if (CollectionUtil.isEmpty(resultMapList)) {
            return new LinkedList<>();
        }
        return resultMapList.stream()
                .map(m -> BeanUtil.toBeanIgnoreError(m, clazz))
                .collect(Collectors.toList());
    }

    public <T> List<T> getKeyValueList(String tableName, String keyColumnName, String valueColumnName, List<FilterData> filterDataList, List<FilterData> orFilterDataList, String dataPermFilter, Class<T> clazz) {
        log.info("ResDao getKeyValueList : tableName: {} , keyColumnName :{} , valueColumnName : {} , filterDataList :{} , orFilterDataList : {}, dataPermFilter : {}", tableName, keyColumnName, valueColumnName, JSON.toJSONString(filterDataList), JSON.toJSONString(orFilterDataList), dataPermFilter);
        List<Map<String, Object>> resultMapList = resGetKeyValueListDao.getDictDataList(tableName, keyColumnName, valueColumnName, filterDataList, orFilterDataList, dataPermFilter);
        if (CollectionUtil.isEmpty(resultMapList)) {
            return new LinkedList<>();
        }
        return resultMapList.stream()
                .map(m -> BeanUtil.toBeanIgnoreError(m, clazz))
                .collect(Collectors.toList());
    }

    public <T> PageDto<T> getPage(String tableName, List<FilterData> filterDataList, List<FilterData> orFilterDataList, String dataPermFilter, String orderBy, Integer pageNo, Integer pageSize, Class<T> clazz) {
        List<T> result = getList(tableName, filterDataList, orFilterDataList, dataPermFilter, orderBy, pageNo, pageSize, clazz);
        Integer count = getListCount(tableName, filterDataList, orFilterDataList, dataPermFilter);
        PageDto<T> pageDto = new PageDto<>();
        pageDto.setData(result);
        pageDto.setCount(count);
        return pageDto;
    }

    public int batchInsertOrUpdate(List<TableData> tableDataList) {
        return resInsertAndUpdateDao.batchInsertOrUpdate(tableDataList);
    }

    public int batchUpdate(List<TableData> tableDataList) {
        return batchUpdate(tableDataList, null);
    }

    public int update(String tableName,List<ColumnData> updateColumnList,List<FilterData> filterDataList,String dataPermFilter){
        return resInsertAndUpdateDao.update(tableName,updateColumnList,filterDataList,dataPermFilter);
    }

    public int batchUpdate(List<TableData> tableDataList, String dataPermFilter) {
        return resInsertAndUpdateDao.batchUpdate(tableDataList, dataPermFilter);
    }
//    public static final String updateTimeColumnName = "update_time";
//    public static final String sourceIdColumnName = "source_id";
//    public static final String dataStatusColumnName = "data_status";
//    public static final String transformStatusColumnName = "transform_status";
//    public static final String matchedStatusColumnName = "matched_status";
//    public static final String relationIdColumnName = "relation_id";
//    public static final String relationParentIdColumnName = "relation_parent_id";
//    public static final String confirmStatusColumnName = "confirm_status";
//    /**
//     * 关系固定字段
//     */
//    public static final String SOURCE_ID = "source_id";
//    public static final String DATA_STATUS = "data_status";
//    public static final String TRANSFORM_STATUS = "transform_status";
//    public static final String MATCHED_STATUS = "matched_status";
//    public static final String RELATION_ID = "relation_id";
//    public static final String RELATION_PARENT_ID = "relation_parent_id";
//    public static final String CONFIRM_STATUS = "confirm_status";

    public TableData buildTableDataForJob(Integer sourceId, String tableName, String primaryKeyColumnName, String primaryKeyColumnValue) {
        TableData tableData = new TableData(tableName, primaryKeyColumnName, ResId.getResId(sourceId, primaryKeyColumnValue), systemUser);
        tableData.addColumn(ResEntity.SOURCE_ID, sourceId);
        return tableData;
    }

    public <T> void buildTableDataForFetchData(TableData tableData, T resEntity, Class clazz) {
        List<String> fetchDataFieldList = null;
        List<String> cacheValue = LocalCache.getList("getFetchDataFieldListByTableName_" + tableData.tableName, String.class);
        if (CollectionUtil.isNotEmpty(cacheValue)) {
            fetchDataFieldList = cacheValue;
        } else {
            fetchDataFieldList = resEntityDao.getFetchDataFieldListByTableName(tableData.tableName);
            LocalCache.add("getFetchDataFieldListByTableName_" + tableData.tableName, fetchDataFieldList);
        }

        Map<String, FastField> fastFieldMap = FastReflect.getFieldMap(clazz);
        for (String columnName : fetchDataFieldList) {
            FastField fastField = fastFieldMap.get(StrUtil.toCamelCase(columnName));
            if (fastField != null) {
                Object fieldValue = ReflectUtil.getFieldValue(resEntity, fastField.getField());
                if(fieldValue != null) {
                    tableData.addColumn(columnName, fieldValue);
                }
            }
        }

//        for(String columnName : fetchDataFieldList){
//            Field field = ReflectUtil.getField(clazz, StrUtil.toCamelCase(columnName));
//            Object fieldValue = ReflectUtil.getFieldValue(resEntity,field);
//            tableData.addColumn(columnName, fieldValue);
//        }
        log.info("buildTableDataForFetchData : {}", JSON.toJSONString(tableData));
    }

    public <T> void buildTableDataForTransformData(TableData tableData, T resEntity, Class clazz) {
        List<String> transformDataFieldList = null;
        List<String> cacheValue = LocalCache.getList("getTransformDataFieldListByTableName_" + tableData.tableName, String.class);
        if (CollectionUtil.isNotEmpty(cacheValue)) {
            transformDataFieldList = cacheValue;
        } else {
            transformDataFieldList = resEntityDao.getTransformDataFieldListByTableName(tableData.tableName);
            LocalCache.add("getTransformDataFieldListByTableName_" + tableData.tableName, transformDataFieldList);
        }

        Map<String, FastField> fastFieldMap = FastReflect.getFieldMap(clazz);
        for (String columnName : transformDataFieldList) {
            FastField fastField = fastFieldMap.get(StrUtil.toCamelCase(columnName));
            if (fastField != null) {
                Object fieldValue = ReflectUtil.getFieldValue(resEntity, fastField.getField());
                tableData.addColumn(columnName, fieldValue);
            }
        }

//        for(String columnName : transformDataFieldList){
//            Field field = ReflectUtil.getField(clazz, StrUtil.toCamelCase(columnName));
//            Object fieldValue = ReflectUtil.getFieldValue(resEntity,field);
//            tableData.addColumn(columnName, fieldValue);
//        }
        log.info("buildTableDataForTransformData : {}", JSON.toJSONString(tableData));
    }

    public String getTableName(Class clazz) {
        String tableNameStr = null;
        String cacheTableName = LocalCache.get("getTableName_" + clazz.getName());
        if (StrUtil.isNotBlank(cacheTableName)) {
            return cacheTableName;
        } else {
            boolean hasTableName = clazz.isAnnotationPresent(TableName.class);
            if (hasTableName) {
                TableName tableName = (TableName) clazz.getAnnotation(TableName.class);
                tableNameStr = tableName.value();
            }
            if (StrUtil.isAllBlank(tableNameStr)) {
                tableNameStr = StrUtil.toUnderlineCase(clazz.getSimpleName());
            }
            LocalCache.add("getTableName_" + clazz.getName(), tableNameStr);
            return tableNameStr;
        }
    }

    public <T> void getPrimaryKeyColumnList(TableData tableData, T resEntity, Class clazz) {
        String tableName = getTableName(clazz);
        List<String> primaryKeyFieldList = null;
        List<String> cacheValue = LocalCache.getList("getPrimaryKeyFieldListByTableName_" + tableName, String.class);
        if (CollectionUtil.isNotEmpty(cacheValue)) {
            primaryKeyFieldList = cacheValue;
        } else {
            primaryKeyFieldList = resEntityDao.getPrimaryKeyFieldListByTableName(tableName);
            LocalCache.add("getPrimaryKeyFieldListByTableName_" + tableName, primaryKeyFieldList);
        }
        log.info("resEntityDao.getPrimaryKeyFieldListByTableName {} : {}", tableName, JSON.toJSONString(primaryKeyFieldList));
        if (CollectionUtil.isNotEmpty(primaryKeyFieldList)) {
            for (String columnName : primaryKeyFieldList) {
                Field field = ReflectUtil.getField(clazz, StrUtil.toCamelCase(columnName));
                Object fieldValue = ReflectUtil.getFieldValue(resEntity, field);
                tableData.addPrimaryKeyColumn(columnName, fieldValue);
            }
        } else {
            String primaryKeyFieldColumnName = null;
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                boolean hasTableId = field.isAnnotationPresent(TableId.class);
                if (hasTableId) {
                    TableId tableId = field.getAnnotation(TableId.class);
                    primaryKeyFieldColumnName = tableId.value();
                    break;
                }
            }

            Map<String, FastField> fastFieldMap = FastReflect.getFieldMap(clazz);

            FastField fastField = fastFieldMap.get(StrUtil.toCamelCase(primaryKeyFieldColumnName));
            if (fastField != null) {
                Object fieldValue = ReflectUtil.getFieldValue(resEntity, fastField.getField());
                tableData.addColumn(primaryKeyFieldColumnName, fieldValue);
            }

        }
        log.info("getPrimaryKeyColumnList {} : {}", tableName, JSON.toJSONString(tableData));
    }

    public String getPrimaryKeyColumnName(Class clazz) {
        String tableName = getTableName(clazz);
        String cacheValue = LocalCache.get("getPrimaryKeyColumnName_" + tableName);
        if (StrUtil.isNotBlank(cacheValue)) {
            return cacheValue;
        }
        String primaryKeyFieldColumnName = resEntityDao.getPrimaryKeyFieldNameByTableName(tableName);
        log.info("resEntityDao.getPrimaryKeyFieldNameByTableName {} : {}", tableName, primaryKeyFieldColumnName);
        LocalCache.add("getPrimaryKeyColumnName_" + tableName, primaryKeyFieldColumnName);
        return primaryKeyFieldColumnName;
    }

}
