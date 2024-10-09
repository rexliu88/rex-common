package com.github.rexliu88.service;

import com.github.rexliu88.easydb.model.FilterData;
import com.github.rexliu88.common.User;
import com.github.rexliu88.dto.PageDto;
import com.github.rexliu88.dto.SourceInfoDto;

import java.util.List;
import java.util.Set;

/**
 * 资源实体类型服务接口
 * @param <T>
 */
public interface ResEntityService <T> {
    /**
     * 页面查询来源列表
     * 1 雷速足球 2 雷速篮球 等
     * @return
     */
    List<SourceInfoDto> sourcelist();

    /**
     * 页面列表分页查询
     * @param sourceId 来源Id
     * @param filterDataList 页面输入 - 精确查询
     * @param orFilterDataList 页面输入 - 模糊查询
     * @param page 页码
     * @param size 页大小
     * @param clazz 资源实体类型
     * @return 分页列表
     */
    PageDto<T> list(Integer sourceId, List<FilterData> filterDataList, List<FilterData> orFilterDataList, Integer page, Integer size, Class<T> clazz);

    /**
     * 页面关键词搜索，查询第一页最多200条数据。
     * @param sourceId 来源Id
     * @param orFilterDataList 关键词查询字段
     * @param clazz 资源实体类型
     * @return 分页列表
     */
    List<T> search(Integer sourceId, List<FilterData> orFilterDataList,String dataPermFilter, Class<T> clazz);

    /**
     * 页面批量确认操作
     * @param sourceId 来源Id
     * @param resIdList 资源Id列表
     * @param curUser 当前操作用户
     * @param clazz 资源实体类型
     * @return 数据库更新数量
     */
    Integer batchConfirm(Integer sourceId, List<String> resIdList, User curUser, Class<T> clazz);

    /**
     * 页面批量新增操作
     * 调用ResEntityServiceImpl 中抽象方法 void remoteServiceBatchAdd(List<T> recordList);
     * 远程批量新增主库数据，并主库返回的关系Id 保存到实体类型对象中。
     * 再将 实体对象列表 保存到数据库。
     * @param sourceId 来源Id
     * @param resIdSet 资源Id列表
     * @param curUser 当前操作用户
     * @param clazz 资源实体类型
     * @return 数据库更新数量
     */
    Integer batchAdd(Integer sourceId, Set<String> resIdSet, User curUser, Class<T> clazz);

    /**
     * 页面确认操作
     * @param sourceId 来源Id
     * @param resId 资源Id
     * @param curUser 当前操作用户
     * @param clazz 资源实体类型
     * @return 数据库更新数量
     */
    Integer confirm(Integer sourceId,String resId, User curUser, Class<T> clazz);

    /**
     * 页面匹配操作
     * @param sourceId 来源Id
     * @param resId 资源Id
     * @param relationId 关系主键编码
     * @param relationParentId 关系父编码
     * @param curUser 当前操作用户
     * @param clazz 资源实体类型
     * @return 数据库更新数量
     */
    Integer matched(Integer sourceId, String resId, String relationId, String relationParentId, User curUser, Class<T> clazz);

    /**
     * 页面解除绑定关系
     * 调用ResEntityServiceImpl 中抽象方法 Integer getSubCount(T resEntity);
     * 子对象数量查询接口，确认下级没有子对象才能解绑当前对象。
     * 实现 不同资源实体类型，对不同子对象数量查询
     * @param sourceId 来源Id
     * @param resId 资源Id
     * @param curUser 当前操作用户
     * @param clazz 资源实体类型
     * @return 数据库更新数量
     */
    Integer unbind(Integer sourceId, String resId, User curUser, Class<T> clazz);

    /**
     * Job批量新增或更新 - 抓取数据保存
     * 抓取最后一步 将Job抓取到的转换数据保存到临时库
     * 调用ResEntityServiceImpl 中抽象方法 void buildTableDataForFetchData(TableData tableData, T resEntity);
     * 实现 不同资源实体类型，不同抓取字段的保存。
     *
     * 前面的Job步骤在JobHandler中实现部分，它还调用了抽象方法，还有在 具体来源接口中的 抓取抽象实现，具体接口 抓取数据的字段映射。
     * @param sourceId 来源Id
     * @param resEntityList 资源实体对象列表
     */
    void batchInsertOrUpdateForFetchData(Integer sourceId, List<T> resEntityList,Class<T> clazz);

    /**
     * Job批量更新 - 转换数据保存
     * 转换最后一步 将Job转换后的转换数据保存到临时库
     * 调用ResEntityServiceImpl 中抽象方法 void buildTableDataForTransformData(TableData tableData, T resEntity);
     * 实现 不同资源实体类型，不同转换字段的保存。
     *
     * 前面的Job步骤在JobHandler中实现部分，它还调用了抽象方法，还有在 具体来源接口中的 转换抽象实现，具体接口 枚举类型转换等。
     * @param sourceId 来源Id
     * @param resEntityList 资源实体对象列表
     */
    void batchUpdateForTransformData(Integer sourceId, List<T> resEntityList,Class<T> clazz);

    /**
     * Job批量更新 - 匹配数据保存
     * 匹配最后一步 将Job匹配后的匹配关系保存到临时库
     * 不同资源实体类型，相同匹配字段的保存。
     *
     * 前面的Job步骤在JobHandler中，还有调用 ResEntityServiceImpl 中抽象方法 void remoteServiceMatchedResEntity(T resEntity);
     * 调用 远程查询实体数据 进行自动匹配计算，将 匹配的关系Id 和 匹配状态 确认状态 保存到实体类型对象中。
     * 到这里来保存数据
     * @param sourceId 来源Id
     * @param recordList 资源实体对象列表
     */
    void batchUpdateForMatchedData(Integer sourceId,List<T> recordList,Class<T> clazz);

    /**
     * Job批量更新 - 写主库数据保存
     * 写主库最后一步 将Job写主库后的数据状态保存到临时库
     * 不同资源实体类型，相同数据状态字段的保存。
     *
     * 前面的Job步骤在JobHandler中，还有调用 ResEntityServiceImpl 中抽象方法 void remoteServiceWritemasterResEntity(T resEntity);
     * 调用 远程将实体数据写入主库，并更新 数据状态 保存到实体类型对象中。
     * 到这里来保存数据
     * @param sourceId 来源Id
     * @param recordList 资源实体对象列表
     */
    void batchUpdateForWriteMasterData(Integer sourceId,List<T> recordList,Class<T> clazz);

    /**
     * Job查询数据 - 更加数据状态 分页查询列表数据
     * @param sourceId 来源Id
     * @param dataStatus 数据状态
     * @param page 页码
     * @param size 页大小
     * @param clazz 资源实体类型
     * @return 分页列表
     */
    PageDto<T> getPageByDataStatus(Integer sourceId, Integer dataStatus, Integer page, Integer size, Class<T> clazz);
}
