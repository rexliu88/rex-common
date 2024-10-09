package com.auxgroup.adp.commons.utils.mapper;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.Assert;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.core.toolkit.ReflectionKit;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;

import java.io.Serializable;
import java.util.*;

public interface BaseMapperPlus<M, T, V> extends BaseMapper<T> {
    default <C> C copyProperties(T obj,Class<C> voClass) {
        if(ObjectUtil.isNull(obj)) {
            return null;
        }
        C res = ReflectUtil.newInstance(voClass);
        BeanUtil.copyProperties(obj,res);
        return res;
    }

    default <C> List<C> copyPropertiesByList(List<T> objList, Class<C> voClass) {
        List<C> resList = new ArrayList<>();
        if(CollUtil.isEmpty(objList)) {
            return resList;
        }
        for (T obj : objList) {
            if(ObjectUtil.isNull(obj)) {
                continue;
            }
            C res = copyProperties(obj,voClass);
            if(ObjectUtil.isNotNull(obj)) {
                resList.add(res);
            }
        }
        return resList;
    }

    Log log = LogFactory.getLog(BaseMapperPlus.class);
    int DEFAULT_BATCH_SIZE = 1000;

    default Class<V> currentVoClass() {
        return (Class) ReflectionKit.getSuperClassGenericType(this.getClass(), BaseMapperPlus.class, 2);
    }

    default Class<T> currentModelClass() {
        return (Class)ReflectionKit.getSuperClassGenericType(this.getClass(), BaseMapperPlus.class, 1);
    }

    default Class<M> currentMapperClass() {
        return (Class)ReflectionKit.getSuperClassGenericType(this.getClass(), BaseMapperPlus.class, 0);
    }

    default List<T> selectList() {
        return this.selectList(new QueryWrapper());
    }

    default boolean insertBatch(Collection<T> entityList) {
        return this.insertBatch(entityList, 1000);
    }

    default boolean updateBatchById(Collection<T> entityList) {
        return this.updateBatchById(entityList, 1000);
    }

    default boolean insertOrUpdateBatch(Collection<T> entityList) {
        return this.insertOrUpdateBatch(entityList, 1000);
    }

    default boolean insertBatch(Collection<T> entityList, int batchSize) {
        String sqlStatement = SqlHelper.getSqlStatement(this.currentMapperClass(), SqlMethod.INSERT_ONE);
        return SqlHelper.executeBatch(this.currentModelClass(), log, entityList, batchSize, (sqlSession, entity) -> {
            sqlSession.insert(sqlStatement, entity);
        });
    }

    default boolean updateBatchById(Collection<T> entityList, int batchSize) {
        String sqlStatement = SqlHelper.getSqlStatement(this.currentMapperClass(), SqlMethod.UPDATE_BY_ID);
        return SqlHelper.executeBatch(this.currentModelClass(), log, entityList, batchSize, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap();
            param.put("et", entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    default boolean insertOrUpdateBatch(Collection<T> entityList, int batchSize) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(this.currentModelClass());
        Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);
        String keyProperty = tableInfo.getKeyProperty();
        Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!", new Object[0]);
        return SqlHelper.saveOrUpdateBatch(this.currentModelClass(), this.currentMapperClass(), log, entityList, batchSize, (sqlSession, entity) -> {
            Object idVal = tableInfo.getPropertyValue(entity, keyProperty);
            String sqlStatement = SqlHelper.getSqlStatement(this.currentMapperClass(), SqlMethod.SELECT_BY_ID);
            return StringUtils.checkValNull(idVal) || CollectionUtils.isEmpty(sqlSession.selectList(sqlStatement, entity));
        }, (sqlSession, entity) -> {
            MapperMethod.ParamMap<T> param = new MapperMethod.ParamMap();
            param.put("et", entity);
            String sqlStatement = SqlHelper.getSqlStatement(this.currentMapperClass(), SqlMethod.UPDATE_BY_ID);
            sqlSession.update(sqlStatement, param);
        });
    }

    default boolean insertOrUpdate(T entity) {
        if (null == entity) {
            return false;
        } else {
            TableInfo tableInfo = TableInfoHelper.getTableInfo(this.currentModelClass());
            Assert.notNull(tableInfo, "error: can not execute. because can not find cache of TableInfo for entity!", new Object[0]);
            String keyProperty = tableInfo.getKeyProperty();
            Assert.notEmpty(keyProperty, "error: can not execute. because can not find column for id from entity!", new Object[0]);
            Object idVal = tableInfo.getPropertyValue(entity, tableInfo.getKeyProperty());
            return !StringUtils.checkValNull(idVal) && !Objects.isNull(this.selectById((Serializable)idVal)) ? this.updateById(entity) > 0 : this.insert(entity) > 0;
        }
    }

    default V selectVoById(Serializable id) {
        return this.selectVoById(id, this.currentVoClass());
    }

    default <C> C selectVoById(Serializable id, Class<C> voClass) {
        T obj = this.selectById(id);
        return ObjectUtil.isNull(obj) ? null : copyProperties(obj,voClass);
    }

    default List<V> selectVoBatchIds(Collection<? extends Serializable> idList) {
        return this.selectVoBatchIds(idList, this.currentVoClass());
    }

    default <C> List<C> selectVoBatchIds(Collection<? extends Serializable> idList, Class<C> voClass) {
        List<T> list = this.selectBatchIds(idList);
        return (List)(CollUtil.isEmpty(list) ? CollUtil.newArrayList(new Object[0]) : copyPropertiesByList(list,voClass));
    }

    default List<V> selectVoByMap(Map<String, Object> map) {
        return this.selectVoByMap(map, this.currentVoClass());
    }

    default <C> List<C> selectVoByMap(Map<String, Object> map, Class<C> voClass) {
        List<T> list = this.selectByMap(map);
        return (List)(CollUtil.isEmpty(list) ? CollUtil.newArrayList(new Object[0]) : copyPropertiesByList(list,voClass));
    }

    default V selectVoOne(Wrapper<T> wrapper) {
        return this.selectVoOne(wrapper, this.currentVoClass());
    }

    default <C> C selectVoOne(Wrapper<T> wrapper, Class<C> voClass) {
        T obj = this.selectOne(wrapper);
        return ObjectUtil.isNull(obj) ? null : copyProperties(obj,voClass);
    }

    default List<V> selectVoList(Wrapper<T> wrapper) {
        return this.selectVoList(wrapper, this.currentVoClass());
    }

    default <C> List<C> selectVoList(Wrapper<T> wrapper, Class<C> voClass) {
        List<T> list = this.selectList(wrapper);
        return (List)(CollUtil.isEmpty(list) ? CollUtil.newArrayList(new Object[0]) : copyPropertiesByList(list,voClass));
    }

    default <P extends IPage<V>> P selectVoPage(IPage<T> page, Wrapper<T> wrapper) {
        return this.selectVoPage(page, wrapper, this.currentVoClass());
    }

    default <C, P extends IPage<C>> P selectVoPage(IPage<T> page, Wrapper<T> wrapper, Class<C> voClass) {
        IPage<T> pageData = this.selectPage(page, wrapper);
        IPage<C> voPage = new Page(pageData.getCurrent(), pageData.getSize(), pageData.getTotal());
        if (CollUtil.isEmpty(pageData.getRecords())) {
            return (P)voPage;
        } else {
            voPage.setRecords(copyPropertiesByList(pageData.getRecords(),voClass));
            return (P)voPage;
        }
    }
}
