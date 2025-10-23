package com.auxdemo.adp.commons.group.excel.listener;

import com.alibaba.excel.read.listener.ReadListener;
import com.auxdemo.adp.commons.group.excel.result.ExcelResult;

/**
 * Excel监听器接口，继承自阿里巴巴Excel读取监听器
 * 用于处理Excel文件读取过程中的事件监听，并提供获取Excel处理结果的方法
 *
 * @param <T> 泛型类型，表示Excel中每行数据对应的实体类类型
 */
public interface ExcelListener<T> extends ReadListener<T> {
    /**
     * 获取Excel处理结果
     *
     * @return ExcelResult<T> Excel处理结果对象，包含解析后的数据列表和其他相关信息
     */
    ExcelResult<T> getExcelResult();
}
