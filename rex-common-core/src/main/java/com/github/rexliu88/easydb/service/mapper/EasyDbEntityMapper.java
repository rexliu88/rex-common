package com.github.rexliu88.easydb.service.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.auxgroup.adp.commons.group.beans.AuxResponse;
import com.auxgroup.adp.commons.utils.AuxBeanUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.HavingSegmentList;
import com.baomidou.mybatisplus.core.conditions.segments.MergeSegments;
import com.baomidou.mybatisplus.core.conditions.segments.OrderBySegmentList;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.rexliu88.dto.PageDto;
import com.github.rexliu88.easydb.dao.*;
import com.github.rexliu88.easydb.model.ColumnData;
import com.github.rexliu88.easydb.model.FilterData;
import com.github.rexliu88.easydb.model.TableData;
import com.github.rexliu88.easydb.utils.LocalCache;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import com.github.rexliu88.easydb.constant.CacheKeyEnum;

@Component
@Slf4j
public class EasyDbEntityMapper<T> {
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

    private Field getTableIdField(Class<T> clazz){
        for (Field field : ReflectUtil.getFields(clazz)) {
            field.setAccessible(true);
            boolean hasTableId = field.isAnnotationPresent(TableId.class);
            if(hasTableId){
                return field;
            }
        }
        throw new RuntimeException("实体未设置主键字段");
    }

    private String getTableIdColumnName(Class<T> clazz){
        Field field = getTableIdField(clazz);
        if(Objects.nonNull(field)){
            TableId tableId = field.getAnnotation(TableId.class);
            return tableId.value();
        }
        throw new RuntimeException("实体未设置主键字段");
    }

    private String getTableIdColumnValue(Class<T> clazz, T entity){
        Field field = getTableIdField(clazz);
        String tableIdValue = null;
        if(Objects.nonNull(field)){
            try {
                tableIdValue = (String)field.get(entity);//得到此属性的值
            } catch (IllegalAccessException e) {
                throw new RuntimeException("实体获取主键字段值错误",e);
            }
            if(StrUtil.isNotBlank(tableIdValue)) {
                return tableIdValue;
            }
        }
        throw new RuntimeException("实体未设置主键字段值");
    }

    private String getTableName(Class<T> clazz){
        boolean hasTableName = clazz.isAnnotationPresent(TableName.class);
        if(hasTableName){
            TableName tableName = clazz.getAnnotation(TableName.class);
            return tableName.value();
        }
        return StrUtil.toUnderlineCase(clazz.getName());
    }

    public T selectById(Serializable id, Class<T> entityClass,String dataPermFilter) {
        String tableName = getTableName(entityClass);

        String tableIdColumnName = getTableIdColumnName(entityClass);
        List<ColumnData> tableIdColumnDataList = new ArrayList<>();
        ColumnData tableIdColumnData = new ColumnData(tableIdColumnName, id);
        tableIdColumnDataList.add(tableIdColumnData);

        List<String> selectFieldList = LocalCache.getList( CacheKeyEnum.TABLE.getCode()  + tableName,String.class);
        if(CollectionUtil.isEmpty(selectFieldList)) {
            selectFieldList = resEntityDao.getFieldListByTableName(tableName);
            LocalCache.add(CacheKeyEnum.TABLE.getCode()  + tableName, selectFieldList, Long.parseLong(CacheKeyEnum.TABLE.getName()));
        }

        log.info("ResDao getOneByPrimaryKey tableName: {} , primaryKeyColumn :{} , dataPermFilter : {} , selectFieldList : {}", tableName, JSON.toJSONString(tableIdColumnDataList), dataPermFilter, JSON.toJSONString(selectFieldList));
        Map<String, Object> resultMap = resEntityDao.getOneByPrimaryKey(tableName, selectFieldList, tableIdColumnDataList, dataPermFilter);
        return BeanUtil.toBeanIgnoreError(resultMap, entityClass);
    }
    public List<T> selectList(List<FilterData> filterDataList,
                              List<FilterData> orFilterDataList,
                              String dataPermFilter,
                              String orderBy,
                              Integer pageNo,
                              Integer pageSize,
                              Class<T> entityClass){
        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize != null && pageSize > 5000) {
            pageSize = 5000;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 50;
        }

        String tableName = getTableName(entityClass);

        List<String> selectFieldList = LocalCache.getList( CacheKeyEnum.TABLE.getCode()  + tableName,String.class);
        if(CollectionUtil.isEmpty(selectFieldList)) {
            selectFieldList = resEntityDao.getFieldListByTableName(tableName);
            LocalCache.add(CacheKeyEnum.TABLE.getCode()  + tableName, selectFieldList, Long.parseLong(CacheKeyEnum.TABLE.getName()));
        }

        log.info("ResDao getList tableName: {} , filterDataList :{} , orFilterDataList : {} , dataPermFilter : {},orderBy : {} ,pageNo : {} ,pageSize : {}, selectFieldList : {}", tableName, JSON.toJSONString(filterDataList), JSON.toJSONString(orFilterDataList), dataPermFilter, orderBy, pageNo, pageSize, JSON.toJSONString(selectFieldList));
        List<Map<String, Object>> resultMapList = resGetListDao.getList(tableName, selectFieldList, filterDataList, orFilterDataList, dataPermFilter, orderBy, pageNo, pageSize);
        if (CollectionUtil.isEmpty(resultMapList)) {
            return new LinkedList<>();
        }
        return resultMapList.stream()
                .map(m -> BeanUtil.toBeanIgnoreError(m, entityClass))
                .collect(Collectors.toList());
    }

    public Integer selectCount(List<FilterData> filterDataList,
                                List<FilterData> orFilterDataList,
                                String dataPermFilter,
                                Class<T> entityClass){
        String tableName = getTableName(entityClass);
        log.info("ResDao getListCount tableName: {} , filterDataList :{} , orFilterDataList : {} , dataPermFilter : {}", tableName, JSON.toJSONString(filterDataList), JSON.toJSONString(orFilterDataList), dataPermFilter);
        return resGetListCountDao.getListCount(tableName, filterDataList, orFilterDataList, dataPermFilter);
    }

    public IPage<T> selectPage( List<FilterData> filterDataList, List<FilterData> orFilterDataList, String dataPermFilter, String orderBy, Integer pageNo, Integer pageSize, Class<T> clazz) {
        if (pageNo == null || pageNo < 1) {
            pageNo = 1;
        }
        if (pageSize != null && pageSize > 5000) {
            pageSize = 5000;
        }
        if (pageSize == null || pageSize < 1) {
            pageSize = 50;
        }
        List<T> result = selectList(filterDataList, orFilterDataList, dataPermFilter, orderBy, pageNo, pageSize, clazz);
        Integer count = selectCount(filterDataList, orFilterDataList, dataPermFilter, clazz);


        IPage<T> entityPage = new Page(pageNo, pageSize, count);
        entityPage.setRecords(result);
        return entityPage;
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
}
