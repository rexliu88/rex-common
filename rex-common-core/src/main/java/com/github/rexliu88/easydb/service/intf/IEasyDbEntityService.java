package com.github.rexliu88.easydb.service.intf;

import java.util.Collection;
import java.util.List;

import com.auxgroup.adp.commons.group.beans.AuxResponse;
import com.auxgroup.adp.commons.utils.page.QueryBody;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface IEasyDbEntityService<T> {
    AuxResponse<T> queryById(Long id);
    AuxResponse<Page<T>> queryPageList(QueryBody<T> entityQueryBody);
    AuxResponse<List<T>> queryList(T entity);
    AuxResponse<Long> insertByDto(T entity);
    AuxResponse<Boolean> updateByDto(T entity);
    AuxResponse<Boolean> deleteWithValidByIds(Collection<Long> ids);
    AuxResponse<Boolean> batchInsertByDtoList(List<T> entityList);
}
