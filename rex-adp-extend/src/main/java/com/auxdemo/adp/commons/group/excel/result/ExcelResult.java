package com.auxdemo.adp.commons.group.excel.result;

import java.util.List;

public interface ExcelResult<T> {
    List<T> getList();

    List<String> getErrorList();

    String getAnalysis();
}
