package com.auxdemo.adp.commons.group.excel.result;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
@Data
public class DefautExcelResult<T> implements ExcelResult<T> {
    private List<T> list;
    private List<String> errorList;

    @Override
    public String getAnalysis() {
        int successCount = this.list.size();
        int errorCount = this.errorList.size();
        if (successCount == 0) {
            return "读取失败，未解析到数据";
        } else {
            return errorCount == 0 ?
                    StrUtil.format("恭喜您，全部读取成功！共{}条", new Object[]{successCount})
                    : StrUtil.format("读取部分成功，共{}条成功，{}条失败", new Object[]{successCount, errorCount});
        }
    }

    public DefautExcelResult(List<T> list, List<String> errorList) {
        this.list = list != null ? list : new ArrayList<>();
        this.errorList = errorList != null ? errorList : new ArrayList<>();
    }

    public DefautExcelResult() {
        this.list = new ArrayList<>();
        this.errorList = new ArrayList<>();
    }
}
