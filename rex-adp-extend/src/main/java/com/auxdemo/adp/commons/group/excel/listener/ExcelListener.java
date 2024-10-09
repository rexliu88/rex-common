package com.auxdemo.adp.commons.group.excel.listener;

import com.alibaba.excel.read.listener.ReadListener;
import com.auxdemo.adp.commons.group.excel.result.ExcelResult;

public interface ExcelListener<T> extends ReadListener<T> {
    ExcelResult<T> getExcelResult();
}
