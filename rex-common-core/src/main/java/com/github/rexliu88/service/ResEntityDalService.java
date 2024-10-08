package com.github.rexliu88.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.annotation.TableId;
import com.github.rexliu88.common.*;
import com.github.rexliu88.constant.ResDataStatus;
import com.github.rexliu88.dto.PageDto;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * 资源实体Dal服务
 */
public interface ResEntityDalService<T extends ResEntity> {
    ResDao getResDao();

    default T getOne(Integer sourceId,String resId) {
        List<FilterData> filterDataList = new ArrayList<>();
        filterDataList.add(new FilterData(getPrimaryKeyColumnName(),resId));
        return getOne(sourceId,filterDataList,null,null,null);
    }
    default T getOne(Integer sourceId,List<FilterData> filterDataList, List<FilterData> orFilterDataList, String dataPermFilter, String orderBy) {
        List<T> list = getList(sourceId,filterDataList,orFilterDataList,dataPermFilter,orderBy,1);
        if(CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }
        return null;
    }
    default Map<String,T> getMap(Integer sourceId,List<FilterData> filterDataList, List<FilterData> orFilterDataList, String dataPermFilter, String orderBy, Integer limit) {
        List<T> list = getList(sourceId,filterDataList,orFilterDataList,dataPermFilter,orderBy,limit);
        Map<String,T> stringTMap = new HashMap<>();
        if(CollectionUtil.isNotEmpty(list)) {
            for (T entity : list) {
                stringTMap.put(entity.getPrimaryKeyColumnValue(),entity);
            }
        }
        return stringTMap;
    }
    default Map<String,T> getMap(Integer sourceId,Set<String> resIdSet) {
        List<T> list = getList(sourceId,resIdSet);
        Map<String,T> stringTMap = new HashMap<>();
        if(CollectionUtil.isNotEmpty(list)) {
            for (T entity : list) {
                stringTMap.put(entity.getPrimaryKeyColumnValue(),entity);
            }
        }
        return stringTMap;
    }
    default List<T> getList(Integer sourceId,Set<String> resIdSet) {
        List<FilterData> filterDataList = new ArrayList<>();
        filterDataList.add(new FilterData(getPrimaryKeyColumnName(), FieldFilterType.IN_LIST_FILTER,resIdSet));
        return getList(sourceId,filterDataList,null,null,null,1000);
    }
    default List<T> getList(Integer sourceId,List<FilterData> filterDataList, List<FilterData> orFilterDataList, String dataPermFilter, String orderBy,Integer limit) {
        filterDataList.add(new FilterData(ResEntity.SOURCE_ID, sourceId));
        return getResDao().getList(getTableName(),filterDataList,orFilterDataList,dataPermFilter,orderBy,1,limit,getResEntityClass());
    }

    default PageDto<T> getPage(Integer sourceId,List<FilterData> filterDataList, List<FilterData> orFilterDataList, String dataPermFilter, String orderBy, Integer page, Integer size){
        filterDataList.add(new FilterData(ResEntity.SOURCE_ID, sourceId));
        PageDto<T> result = getResDao().getPage(getTableName(), filterDataList, orFilterDataList, dataPermFilter, orderBy, page, size, getResEntityClass());
        return result;
    }
    default Integer getCount(Integer sourceId,List<FilterData> filterDataList, List<FilterData> orFilterDataList, String dataPermFilter) {
        filterDataList.add(new FilterData(ResEntity.SOURCE_ID, sourceId));
        return getResDao().getListCount(getTableName(),filterDataList,orFilterDataList,dataPermFilter);
    }

    /**
     * 更新绑定关系
     * 页面操作
     * 1） 单条确认
     * 2） 批量确认
     * 3） 手动匹配
     * 4） 批量匹配
     * 5） 批量新增(关系保存)
     * 6） 解绑关系
     * JOB操作
     * 1)  自动匹配
     * @param resEntityList
     */
    default void updateBindRelation(Integer sourceId,List<T> resEntityList){
        if (CollectionUtil.isEmpty(resEntityList)) {
            return;
        }
        List<TableData> tableDataList = new ArrayList<>();
        for (T resEntity : resEntityList) {
            if (resEntity.getMatchedStatus() != null && resEntity.getConfirmStatus() !=null) {
                //匹配状态 -1 解绑关系、没有系统匹配失败这一事 0 未匹配 1 系统匹配 2 用户匹配
                //确认状态 0 未确认 1 已确认
                TableData tableData = getResDao().buildTableDataForJob(sourceId, resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), resEntity.getPrimaryKeyColumnValue());
                // 关系
                tableData.addColumn(ResEntity.RELATION_ID, resEntity.getRelationId());
                //匹配状态
                tableData.addColumn(ResEntity.MATCHED_STATUS, resEntity.getMatchedStatus());
                tableData.addColumn(ResEntity.CONFIRM_STATUS, resEntity.getConfirmStatus());
                //数据状态
                if (resEntity.getDataStatus() != null) {
                    tableData.addColumn(ResEntity.DATA_STATUS, resEntity.getDataStatus());
                }
                tableData.preUpdate();
                tableDataList.add(tableData);
            }
        }
        getResDao().batchUpdate(tableDataList);
    }

    /**
     * 更新数据状态
     * JOB操作
     * 1) 写主库
     * @param resEntityList
     */
    default void updateDataStatus(Integer sourceId,List<T> resEntityList){
        if (CollectionUtil.isEmpty(resEntityList)) {
            return;
        }
        List<TableData> tableDataList = new ArrayList<>();
        for (T resEntity : resEntityList) {
            //数据状态
            if (resEntity.getDataStatus() != null) {
                TableData tableData = getResDao().buildTableDataForJob(sourceId, resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), resEntity.getPrimaryKeyColumnValue());
                tableData.addColumn(ResEntity.DATA_STATUS, resEntity.getDataStatus());
                tableData.preUpdate();
                tableDataList.add(tableData);
            }
        }
        getResDao().batchUpdate(tableDataList);
    }

    /**
     * 更新数据状态
     * JOB操作
     * 1) 写主库
     */
    default void updateByDataStatus(Integer sourceId,Set<String> resIds,Integer dateStatus){
        if (CollectionUtil.isEmpty(resIds)) {
            return;
        }
        List<TableData> tableDataList = new ArrayList<>();
        for (String resId : resIds) {
            //数据状态
            TableData tableData = getResDao().buildTableDataForJob(sourceId, getTableName(), getPrimaryKeyColumnName(), resId);
            tableData.addColumn(ResEntity.DATA_STATUS, dateStatus);
            tableData.preUpdate();
            tableDataList.add(tableData);
        }
        getResDao().batchUpdate(tableDataList);
    }

    /**
     * 批量新增或更新
     * @param resEntityList
     */
    default void batchInsertOrUpdate(Integer sourceId,List<T> resEntityList) {
        if (CollectionUtil.isEmpty(resEntityList)) {
            return;
        }
        List<TableData> tableDataList = new ArrayList<>();
        for (T resEntity : resEntityList) {
            TableData tableData = getResDao().buildTableDataForJob(sourceId, resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), resEntity.getPrimaryKeyColumnValue());
            getResDao().buildTableDataForFetchData(tableData, resEntity, getResEntityClass());
            //数据状态 外部都是赋值进来的 这边补一下
            if (resEntity.getDataStatus() == null) {
                tableData.addColumn(ResEntity.DATA_STATUS, ResDataStatus.TRANSFORM_COMPLETED);
            } else {
                tableData.addColumn(ResEntity.DATA_STATUS, resEntity.getDataStatus());
            }
            tableData.preInsert();
            tableDataList.add(tableData);
        }
        getResDao().batchInsertOrUpdate(tableDataList);
    }

    default List<T> getResEntityListByDataStatus(Integer sourceId,Integer dataStatus,Integer limit){
        List<FilterData> filterDataList = new ArrayList<>();
        filterDataList.add(new FilterData(ResEntity.SOURCE_ID, sourceId));
        filterDataList.add(new FilterData(ResEntity.DATA_STATUS, dataStatus));
        List<T> result = this.getList(sourceId,filterDataList,null,null,ResEntity.UPDATE_TIME + " DESC ",limit);
        return result;
    }

    default Class<T> getResEntityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    default String getTableName(){
        return getResDao().getTableName(getResEntityClass());
    }

    default String getPrimaryKeyColumnName(){
        for (Field field : getResEntityClass().getDeclaredFields()) {
            field.setAccessible(true);
            boolean hasTableId = field.isAnnotationPresent(TableId.class);
            if(hasTableId){
                TableId tableId = field.getAnnotation(TableId.class);
                return tableId.value();
            }
        }
        throw new RuntimeException("实体未设置主键字段");
    }
}
