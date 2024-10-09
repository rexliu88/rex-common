package com.github.rexliu88.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.github.rexliu88.common.*;
import com.github.rexliu88.constant.*;
import com.github.rexliu88.dto.DictItemDto;
import com.github.rexliu88.dto.PageDto;
import com.github.rexliu88.dto.SourceInfoDto;
import com.github.rexliu88.easydb.constant.FieldFilterType;
import com.github.rexliu88.easydb.model.FilterData;
import com.github.rexliu88.easydb.model.TableData;
import com.github.rexliu88.service.CacheService;
import com.github.rexliu88.service.ResEntityDalService;
import com.github.rexliu88.service.ResEntityService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 资源实体关系服务实现基类
 * 每个实体需要继承这个基类，并 实现重写 实体本身类型定制的 抽象方法。
 * 如果 有些抽象方法不需要做，那就继承实现方法为空即可。
 *
 * @param <T>
 */
@Slf4j
@Data
public abstract class ResEntityServiceImpl<T extends ResEntity> implements ResEntityService<T>, ResEntityDalService<T> {
    /**
     * 抽象方法：调用 本地数据关系 或 远程服务 进行实体关系匹配
     *
     * @param resEntity
     */
    public abstract void remoteServiceMatchedResEntity(T resEntity);

    /**
     * 抽象方法：调用 远程服务 将实体数据 写到主库
     *
     * @param resEntityList
     */
    public abstract void remoteServiceWritemasterResEntity(List<T> resEntityList);

    /**
     * 抽象方法：调用 远程服务 将实体数据列表 批量添加到主库，并并将 主库关系Id 保存到实体中
     *
     * @param resEntityList
     */
    public abstract void remoteServiceBatchAdd(List<T> resEntityList);

    /**
     * 抽象方法：调用 本地数据查询 当前实体下 子对象的数量，用于解除绑定关系，子数量为0才能解除绑定关系
     *
     * @param resEntity
     * @return
     */
    public abstract Integer getSubCount(T resEntity);

//    /**
//     * 抽象方法：根据 实体数据 构建 保存抓取数据的字段的 表数据对象
//     * @param tableData 表数据对象
//     * @param resEntity 实体
//     */
//    public abstract void buildTableDataForFetchData(TableData tableData, T resEntity);
//
//    /**
//     * 抽象方法：根据 实体数据 构建 保存转换数据的字段的 表数据对象
//     * @param tableData 表数据对象
//     * @param resEntity 实体
//     */
//    public abstract void buildTableDataForTransformData(TableData tableData, T resEntity);

    @Autowired
    public ResDao resDao;
    @Autowired
    public CacheService cacheService;

    /**
     * 页面查询来源列表
     * 1 雷速足球 2 雷速篮球 等
     *
     * @return
     */
    @Override
    public List<SourceInfoDto> sourcelist() {
        List<DictItemDto> dictItemDtoList = ResSourceId.getList();
        List<SourceInfoDto> sourceInfoDtoList = new ArrayList<>();
        for (DictItemDto dictItemDto : dictItemDtoList) {
            SourceInfoDto sourceInfoDtoVo = new SourceInfoDto();
            sourceInfoDtoVo.setSourceId(dictItemDto.getId());
            sourceInfoDtoVo.setSourceName(dictItemDto.getName());
            sourceInfoDtoVo.setItemId(ResSourceItem.getName(Integer.valueOf(dictItemDto.getId()))!=null?ResSourceItem.getName(Integer.valueOf(dictItemDto.getId())).getItemId():null);
            sourceInfoDtoVo.setItemName(ResSourceItem.getName(Integer.valueOf(dictItemDto.getId()))!=null?ResSourceItem.getName(Integer.valueOf(dictItemDto.getId())).getText():null);
            sourceInfoDtoList.add(sourceInfoDtoVo);
        }
        return sourceInfoDtoList;
    }

    /**
     * 页面列表分页查询
     *
     * @param sourceId         来源Id
     * @param filterDataList   页面输入 - 精确查询
     * @param orFilterDataList 页面输入 - 模糊查询
     * @param page             页码
     * @param size             页大小
     * @param clazz            资源实体类型
     * @return 实体分页列表
     */
    @Override
    public PageDto<T> list(Integer sourceId, List<FilterData> filterDataList, List<FilterData> orFilterDataList, Integer page, Integer size, Class<T> clazz) {
        if (!ResSourceId.isValid(sourceId)) {
            return new PageDto<>();
        }
        // 数据源Id
        filterDataList.add(new FilterData(ResEntity.SOURCE_ID, sourceId));
        PageDto<T> result = resDao.getPage(resDao.getTableName(clazz), filterDataList, orFilterDataList, null, ResEntity.UPDATE_TIME + " DESC ", page, size, clazz);
        return result;
    }

    /**
     * 页面关键词搜索，查询第一页最多200条数据。
     *
     * @param sourceId         来源Id
     * @param orFilterDataList 关键词查询字段
     * @param clazz            资源实体类型
     * @return 实体分页列表
     */
    @Override
    public List<T> search(Integer sourceId, List<FilterData> orFilterDataList,String dataPermFilter, Class<T> clazz) {
        if (!ResSourceId.isValid(sourceId)) {
            return new ArrayList<>();
        }
        List<FilterData> filterDataList = new ArrayList<>();
        // 数据源Id
        filterDataList.add(new FilterData(ResEntity.SOURCE_ID, sourceId));
        List<T> result = resDao.getList(resDao.getTableName(clazz), filterDataList, orFilterDataList, dataPermFilter, ResEntity.UPDATE_TIME + " DESC ", 1, 200, clazz);
        return result;
    }

    /**
     * 页面批量确认操作
     *
     * @param sourceId  来源Id
     * @param resIdList 资源Id列表
     * @param curUser   当前操作用户
     * @param clazz     资源实体类型
     * @return 数据库更新数量
     */
    @Override
    public Integer batchConfirm(Integer sourceId, List<String> resIdList, User curUser, Class<T> clazz) {
        Integer result = 0;
        log.info(" IN {} ", JSON.toJSONString(resIdList));
        if (CollectionUtil.isNotEmpty(resIdList) && ResSourceId.isValid(sourceId)) {
            List<FilterData> filterDataList = new ArrayList<>();
            // 数据源Id
            filterDataList.add(new FilterData(ResEntity.SOURCE_ID, sourceId));
            // 主键Id
            filterDataList.add(new FilterData(resDao.getPrimaryKeyColumnName(clazz), FieldFilterType.IN_LIST_FILTER, new HashSet<>(resIdList)));
            List<T> oldResEntityList = resDao.getList(resDao.getTableName(clazz), filterDataList, null, null, ResEntity.UPDATE_TIME + " DESC ", 1, 200, clazz);
            List<TableData> updateTableDataList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(oldResEntityList)) {
                for (T resEntity : oldResEntityList) {
                    log.info(" 实体 {} ", JSON.toJSONString(resEntity));
                    if (resEntity.getMatchedStatus() == MatchedStatus.MATCHED || resEntity.getConfirmStatus() == ConfirmStatus.UN_CONFIRMED) {
                        log.info(" 确认匹配对象 {} ", JSON.toJSONString(resEntity));
                        TableData tableData = new TableData(resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), ResId.getResId(sourceId, resEntity.getPrimaryKeyColumnValue()), curUser);
                        tableData.addColumn(ResEntity.SOURCE_ID, sourceId);
                        //数据状态
                        tableData.addColumn(ResEntity.CONFIRM_STATUS, ConfirmStatus.CONFIRMED);
                        //todo 关系Id保存
                        tableData.preUpdate();
                        updateTableDataList.add(tableData);
                    }
                }
            }
            if (updateTableDataList.size() > 0) {
                result = resDao.batchUpdate(updateTableDataList, null);
            }
        }
        return result;
    }

    /**
     * 页面批量新增操作
     * 调用ResEntityServiceImpl 中抽象方法 void remoteServiceBatchAdd(List<T> recordList);
     * 远程批量新增主库数据，并主库返回的关系Id 保存到实体类型对象中。
     * 再将 实体对象列表 保存到数据库。
     *
     * @param sourceId 来源Id
     * @param resIdSet 资源Id列表
     * @param curUser  当前操作用户
     * @param clazz    资源实体类型
     * @return 数据库更新数量
     */
    @Override
    public Integer batchAdd(Integer sourceId, Set<String> resIdSet, User curUser, Class<T> clazz) {
        Integer result = 0;
        if (CollectionUtil.isNotEmpty(resIdSet) && ResSourceId.isValid(sourceId)) {
            List<FilterData> filterDataList = new ArrayList<>();
            // 数据源Id
            filterDataList.add(new FilterData(ResEntity.SOURCE_ID, sourceId));
            // 主键Id
            filterDataList.add(new FilterData(resDao.getPrimaryKeyColumnName(clazz), FieldFilterType.IN_LIST_FILTER, resIdSet));
            List<T> oldResEntityList = resDao.getList(resDao.getTableName(clazz), filterDataList, null, null, ResEntity.UPDATE_TIME + " DESC ", 1, 200, clazz);
            List<T> remoteResEntityList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(oldResEntityList)) {
                for (T resEntity : oldResEntityList) {
                    if (StrUtil.isAllBlank(resEntity.getRelationId())) {
                        remoteResEntityList.add(resEntity);
                    }
                }
            }
            if (CollectionUtil.isNotEmpty(remoteResEntityList)) {
                // 外部抽象方法 单条 调用外围接口 批量新增接口  回填 关系主键Id
                remoteServiceBatchAdd(remoteResEntityList);
                log.info(" remoteServiceBatchAdd END ");
            }
            List<TableData> updateTableDataList = new ArrayList<>();
            if (CollectionUtil.isNotEmpty(remoteResEntityList)) {
                for (T resEntity : remoteResEntityList) {
                    if (StrUtil.isNotBlank(resEntity.getRelationId())) {
                        // 将上面的值 更新到数据库
                        TableData tableData = new TableData(resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), ResId.getResId(sourceId, resEntity.getPrimaryKeyColumnValue()), curUser);
                        tableData.addColumn(ResEntity.SOURCE_ID, sourceId);
                        // 关系
                        tableData.addColumn(ResEntity.RELATION_ID, resEntity.getRelationId());
                        tableData.addColumn(ResEntity.RELATION_PARENT_ID, resEntity.getRelationParentId());
                        //数据状态
                        if (StrUtil.isNotBlank(resEntity.getRelationId())) {
                            tableData.addColumn(ResEntity.MATCHED_STATUS, MatchedStatus.MANUAL_MATCHED);
                            tableData.addColumn(ResEntity.CONFIRM_STATUS, ConfirmStatus.CONFIRMED);
                        }
                        tableData.preUpdate();
                        updateTableDataList.add(tableData);
                    }
                }
            }
            if (updateTableDataList.size() > 0) {
                result = resDao.batchUpdate(updateTableDataList, null);
            }
        }
        return result;
    }

    /**
     * 页面确认操作
     *
     * @param sourceId 来源Id
     * @param resId    资源Id
     * @param curUser  当前操作用户
     * @param clazz    资源实体类型
     * @return 数据库更新数量
     */
    @Override
    public Integer confirm(Integer sourceId, String resId, User curUser, Class<T> clazz) {
        Integer result = 0;
        if (ResSourceId.isValid(sourceId)) {
            T resEntity = resDao.getOneByPrimaryKey(resDao.getTableName(clazz), resDao.getPrimaryKeyColumnName(clazz), ResId.getResId(sourceId, resId), null, clazz);
            List<TableData> updateTableDataList = new ArrayList<>();
            if (resEntity.getMatchedStatus() == MatchedStatus.MATCHED && resEntity.getConfirmStatus() == ConfirmStatus.UN_CONFIRMED) {
                TableData tableData = new TableData(resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), ResId.getResId(sourceId, resId), curUser);
                tableData.addColumn(ResEntity.SOURCE_ID, sourceId);
                //数据状态
                tableData.addColumn(ResEntity.CONFIRM_STATUS, ConfirmStatus.CONFIRMED);
                //todo 关系Id保存
                tableData.preUpdate();
                updateTableDataList.add(tableData);
            }
            if (updateTableDataList.size() > 0) {
                result = resDao.batchUpdate(updateTableDataList, null);
            }
        }
        return result;
    }

    /**
     * 页面匹配操作
     *
     * @param sourceId         来源Id
     * @param resId            资源Id
     * @param relationId       关系主键编码
     * @param relationParentId 关系父编码
     * @param curUser          当前操作用户
     * @param clazz            资源实体类型
     * @return 数据库更新数量
     */
    @Override
    public Integer matched(Integer sourceId, String resId, String relationId, String relationParentId, User curUser, Class<T> clazz) {
        Integer result = 0;
        if (ResSourceId.isValid(sourceId)) {
            T resEntity = resDao.getOneByPrimaryKey(resDao.getTableName(clazz), resDao.getPrimaryKeyColumnName(clazz), ResId.getResId(sourceId, resId), null, clazz);
            List<TableData> updateTableDataList = new ArrayList<>();
            if (resEntity != null && (resEntity.getMatchedStatus() == MatchedStatus.UN_MATCHED || resEntity.getMatchedStatus() == MatchedStatus.MATCHED_FAILED)
                    && StrUtil.isAllBlank(resEntity.getRelationId())) {
                TableData tableData = new TableData(resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), ResId.getResId(sourceId, resId), curUser);
                tableData.addColumn(ResEntity.SOURCE_ID, sourceId);
                // 关系
                tableData.addColumn(ResEntity.RELATION_ID, relationId);
                tableData.addColumn(ResEntity.RELATION_PARENT_ID, relationParentId);
                //数据状态
                tableData.addColumn(ResEntity.MATCHED_STATUS, MatchedStatus.MANUAL_MATCHED);
                tableData.addColumn(ResEntity.CONFIRM_STATUS, ConfirmStatus.CONFIRMED);
                //todo 关系Id保存
                tableData.preUpdate();
                updateTableDataList.add(tableData);
            }
            if (updateTableDataList.size() > 0) {
                result = resDao.batchUpdate(updateTableDataList, null);
            }
        }
        return result;
    }

    /**
     * 页面解除绑定关系
     * 调用ResEntityServiceImpl 中抽象方法 Integer getSubCount(T resEntity);
     * 子对象数量查询接口，确认下级没有子对象才能解绑当前对象。
     * 实现 不同资源实体类型，对不同子对象数量查询
     *
     * @param sourceId 来源Id
     * @param resId    资源Id
     * @param curUser  当前操作用户
     * @param clazz    资源实体类型
     * @return 数据库更新数量
     */
    @Override
    public Integer unbind(Integer sourceId, String resId, User curUser, Class<T> clazz) {
        Integer result = 0;
        if (ResSourceId.isValid(sourceId)) {
            log.info("{} {} unbind : {} ,user : {}", ResSourceId.getName(sourceId), resDao.getTableName(clazz), resId, curUser.getName());
            T resEntity = resDao.getOneByPrimaryKey(resDao.getTableName(clazz), resDao.getPrimaryKeyColumnName(clazz), ResId.getResId(sourceId, resId), null, clazz);
            log.info("{} {} unbind : {} ,user : {}, resEntity: {}", ResSourceId.getName(sourceId), resDao.getTableName(clazz), resId, curUser.getName(), JSON.toJSONString((T) resEntity));
            List<TableData> updateTableDataList = new ArrayList<>();
            if (resEntity != null && (resEntity.getMatchedStatus() == MatchedStatus.MATCHED || resEntity.getMatchedStatus() == MatchedStatus.MANUAL_MATCHED)
                    && StrUtil.isAllNotBlank(resEntity.getRelationId())) {
                //外部抽象方法 确认下级没有子对象了，实现 不同资源实体类型，对不同子对象数量查询
                Integer subCount = getSubCount(resEntity);
                if (subCount != null && subCount == 0) {
                    TableData tableData = new TableData(resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), ResId.getResId(sourceId, resId), curUser);
                    tableData.addColumn(ResEntity.SOURCE_ID, sourceId);
                    // 关系
                    tableData.addColumn(ResEntity.RELATION_ID, "");
                    tableData.addColumn(ResEntity.RELATION_PARENT_ID, "");
                    //数据状态
                    tableData.addColumn(ResEntity.MATCHED_STATUS, MatchedStatus.MATCHED_FAILED);
                    tableData.addColumn(ResEntity.CONFIRM_STATUS, ConfirmStatus.UN_CONFIRMED);
                    //todo 关系Id保存 清除
                    tableData.preUpdate();
                    updateTableDataList.add(tableData);
                }
            }
            if (updateTableDataList.size() > 0) {
                result = resDao.batchUpdate(updateTableDataList, null);
            }
        }
        return result;
    }

    /**
     * Job批量新增或更新 - 抓取数据保存
     * 抓取最后一步 将Job抓取到的转换数据保存到临时库
     * 调用ResEntityServiceImpl 中抽象方法 void buildTableDataForFetchData(TableData tableData, T resEntity);
     * 实现 不同资源实体类型，不同抓取字段的保存。
     * <p>
     * 前面的Job步骤在JobHandler中实现部分，它还调用了抽象方法，还有在 具体来源接口中的 抓取抽象实现，具体接口 抓取数据的字段映射。
     *
     * @param sourceId      来源Id
     * @param resEntityList 资源实体对象列表
     */
    @Override
    public void batchInsertOrUpdateForFetchData(Integer sourceId, List<T> resEntityList, Class<T> clazz) {
        log.info("{} {} batchInsertOrUpdateForFetchData START. ", ResSourceId.getName(sourceId));
        if (CollectionUtil.isEmpty(resEntityList) || !ResSourceId.isValid(sourceId)) {
            return;
        }
        List<TableData> tableDataList = new ArrayList<>();
        for (T resEntity : resEntityList) {
            log.info("sourceId : {} , tablename : {} ， primaryKeyColumnName : {} , primaryKeyColumnValue : {} ", sourceId, resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), resEntity.getPrimaryKeyColumnValue());
            TableData tableData = resDao.buildTableDataForJob(sourceId, resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), resEntity.getPrimaryKeyColumnValue());
            // 外部抽象方法 实现不同资源实体类型，不同抓取字段的保存
            //buildTableDataForFetchData(tableData, resEntity);
            resDao.buildTableDataForFetchData(tableData, resEntity, getResEntityClass());
            //数据状态
            if (resEntity.getDataStatus() == null) {
                tableData.addColumn(ResEntity.DATA_STATUS, ResDataStatus.FETCH_COMPLETED);
            } else {
                tableData.addColumn(ResEntity.DATA_STATUS, resEntity.getDataStatus());
            }
            tableData.preInsert();
            tableDataList.add(tableData);
        }
        resDao.batchInsertOrUpdate(tableDataList);
        log.info("{} {} batchInsertOrUpdateForFetchData END. ", ResSourceId.getName(sourceId));
    }

    /**
     * Job批量更新 - 转换数据保存
     * 转换最后一步 将Job转换后的转换数据保存到临时库
     * 调用ResEntityServiceImpl 中抽象方法 void buildTableDataForTransformData(TableData tableData, T resEntity);
     * 实现 不同资源实体类型，不同转换字段的保存。
     * <p>
     * 前面的Job步骤在JobHandler中实现部分，它还调用了抽象方法，还有在 具体来源接口中的 转换抽象实现，具体接口 枚举类型转换等。
     *
     * @param sourceId      来源Id
     * @param resEntityList 资源实体对象列表
     */
    @Override
    public void batchUpdateForTransformData(Integer sourceId, List<T> resEntityList, Class<T> clazz) {
        if (CollectionUtil.isEmpty(resEntityList) || !ResSourceId.isValid(sourceId)) {
            return;
        }
        List<TableData> tableDataList = new ArrayList<>();
        for (T resEntity : resEntityList) {
            TableData tableData = resDao.buildTableDataForJob(sourceId, resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), resEntity.getPrimaryKeyColumnValue());
            // 外部抽象方法 实现不同资源实体类型，不同转换字段的保存
            //buildTableDataForTransformData(tableData, resEntity);
            resDao.buildTableDataForTransformData(tableData, resEntity, getResEntityClass());
            //数据状态
            if (resEntity.getDataStatus() == null || resEntity.getDataStatus() == ResDataStatus.FETCH_COMPLETED) {
                tableData.addColumn(ResEntity.DATA_STATUS, ResDataStatus.TRANSFORM_COMPLETED);
            } else {
                tableData.addColumn(ResEntity.DATA_STATUS, resEntity.getDataStatus());
            }
            tableData.preUpdate();
            tableDataList.add(tableData);
        }
        resDao.batchUpdate(tableDataList);
    }

    /**
     * Job批量更新 - 匹配数据保存
     * 匹配最后一步 将Job匹配后的匹配关系保存到临时库
     * 不同资源实体类型，相同匹配字段的保存。
     * <p>
     * 前面的Job步骤在JobHandler中，还有调用 ResEntityServiceImpl 中抽象方法 void remoteServiceMatchedResEntity(T resEntity);
     * 调用 远程查询实体数据 进行自动匹配计算，将 匹配的关系Id 和 匹配状态 确认状态 保存到实体类型对象中。
     * 到这里来保存数据
     *
     * @param sourceId   来源Id
     * @param recordList 资源实体对象列表
     */
    @Override
    public void batchUpdateForMatchedData(Integer sourceId, List<T> recordList, Class<T> clazz) {
        if (CollectionUtil.isEmpty(recordList) || !ResSourceId.isValid(sourceId)) {
            return;
        }
        List<TableData> tableDataList = new ArrayList<>();
        for (T resEntity : recordList) {
            TableData tableData = resDao.buildTableDataForJob(sourceId, resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), resEntity.getPrimaryKeyColumnValue());
            // 关系
            tableData.addColumn(ResEntity.RELATION_ID, resEntity.getRelationId());
            tableData.addColumn(ResEntity.RELATION_PARENT_ID, resEntity.getRelationParentId());
            //匹配状态
            tableData.addColumn(ResEntity.MATCHED_STATUS, resEntity.getMatchedStatus());
            tableData.addColumn(ResEntity.CONFIRM_STATUS, resEntity.getConfirmStatus());
            //数据状态
            if (resEntity.getDataStatus() == null || resEntity.getDataStatus() == ResDataStatus.TRANSFORM_COMPLETED) {
                tableData.addColumn(ResEntity.DATA_STATUS, ResDataStatus.MATCHED_COMPLETED);
            } else {
                tableData.addColumn(ResEntity.DATA_STATUS, resEntity.getDataStatus());
            }
            tableData.preUpdate();
            tableDataList.add(tableData);
        }
        resDao.batchUpdate(tableDataList);
    }

    /**
     * Job批量更新 - 写主库数据保存
     * 写主库最后一步 将Job写主库后的数据状态保存到临时库
     * 不同资源实体类型，相同数据状态字段的保存。
     * <p>
     * 前面的Job步骤在JobHandler中，还有调用 ResEntityServiceImpl 中抽象方法 void remoteServiceWritemasterResEntity(List<T> resEntityList);
     * 调用 远程将实体数据写入主库，并更新 数据状态 保存到实体类型对象中。
     * 到这里来保存数据
     *
     * @param sourceId   来源Id
     * @param recordList 资源实体对象列表
     */
    @Override
    public void batchUpdateForWriteMasterData(Integer sourceId, List<T> recordList, Class<T> clazz) {
        if (CollectionUtil.isEmpty(recordList) || !ResSourceId.isValid(sourceId)) {
            return;
        }
        List<TableData> tableDataList = new ArrayList<>();
        for (T resEntity : recordList) {
            TableData tableData = resDao.buildTableDataForJob(sourceId, resEntity.getTableName(), resEntity.getPrimaryKeyColumnName(), resEntity.getPrimaryKeyColumnValue());
            //数据状态
            tableData.addColumn(ResEntity.DATA_STATUS, resEntity.getDataStatus());
            tableData.preUpdate();
            tableDataList.add(tableData);
        }
        resDao.batchUpdate(tableDataList);
    }

    /**
     * Job查询数据 - 更加数据状态 分页查询列表数据
     *
     * @param sourceId   来源Id
     * @param dataStatus 数据状态
     * @param page       页码
     * @param size       页大小
     * @param clazz      资源实体类型
     * @return 分页列表
     */
    @Override
    public PageDto<T> getPageByDataStatus(Integer sourceId, Integer dataStatus, Integer page, Integer size, Class<T> clazz) {
        List<FilterData> filterDataList = new ArrayList<>();
        if (ResSourceId.isValid(sourceId)) {
            // 数据源Id
            filterDataList.add(new FilterData(ResEntity.SOURCE_ID, sourceId));
        }

        // 数据状态
        if (dataStatus != null) {
            filterDataList.add(new FilterData(ResEntity.DATA_STATUS, dataStatus));
        }

        //TODO test
//        filterDataList.add(new FilterData("team_id", "res_1_10766"));

        if (filterDataList.size() == 0) {
            filterDataList = null;
        }
        PageDto<T> result = resDao.getPage(resDao.getTableName(clazz), filterDataList, null, null, ResEntity.UPDATE_TIME + " DESC ", page, size, clazz);
        return result;
    }


//    protected String getTableName(Class<T> clazz) {
//        if (clazz == null) {
//            clazz = getResEntityClass();
//        }
//        boolean hasTableName = clazz.isAnnotationPresent(TableName.class);
//        if (hasTableName) {
//            TableName tableName = clazz.getAnnotation(TableName.class);
//            return tableName.value();
//        }
//        return StrUtil.toUnderlineCase(clazz.getSimpleName());
//    }

//    protected String getPrimaryKeyColumnName(Class<T> clazz) {
//        if (clazz == null) {
//            clazz = getResEntityClass();
//        }
//        for (Field field : getResEntityClass().getDeclaredFields()) {
//            field.setAccessible(true);
//            boolean hasTableId = field.isAnnotationPresent(TableId.class);
//            if (hasTableId) {
//                TableId tableId = field.getAnnotation(TableId.class);
//                return tableId.value();
//            }
//        }
//        throw new RuntimeException("实体未设置主键字段");
//    }
}
