package com.github.rexliu88.easydb.service.impl;

import com.auxgroup.adp.commons.group.beans.AuxResponse;
import com.auxgroup.adp.commons.utils.page.QueryBody;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.rexliu88.easydb.service.intf.IEasyDbEntityService;
import java.util.Collection;
import java.util.List;

public class EasyDbEntityService<T> implements IEasyDbEntityService<T> {
    @Override
    public AuxResponse<T> queryById(Long id) {
        return null;
    }

    @Override
    public AuxResponse<Page<T>> queryPageList(QueryBody<T> entityQueryBody) {
        return null;
    }

    @Override
    public AuxResponse<List<T>> queryList(T entity) {
        return null;
    }

    @Override
    public AuxResponse<Long> insertByDto(T entity) {
        return null;
    }

    @Override
    public AuxResponse<Boolean> updateByDto(T entity) {
        return null;
    }

    @Override
    public AuxResponse<Boolean> deleteWithValidByIds(Collection<Long> ids) {
        return null;
    }

    @Override
    public AuxResponse<Boolean> batchInsertByDtoList(List<T> entityList) {
        return null;
    }
}
