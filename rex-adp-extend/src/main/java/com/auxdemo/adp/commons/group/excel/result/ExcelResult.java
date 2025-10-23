package com.auxdemo.adp.commons.group.excel.result;

import java.util.List;

/**
 * Excel处理结果接口
 *
 * @param <T> 泛型类型，表示Excel数据行对应的实体类
 */
public interface ExcelResult<T> {
    /**
     * 获取Excel解析后的数据列表
     *
     * @return 解析成功的数据列表，每个元素对应Excel中的一行数据
     */
    List<T> getList();

    /**
     * 获取Excel解析过程中的错误信息列表
     *
     * @return 解析失败的错误信息列表，每个元素对应一个解析错误
     */
    List<String> getErrorList();

    /**
     * 获取Excel解析分析信息
     *
     * @return 解析过程的分析统计信息，如总行数、成功行数、失败行数等
     */
    String getAnalysis();
}
