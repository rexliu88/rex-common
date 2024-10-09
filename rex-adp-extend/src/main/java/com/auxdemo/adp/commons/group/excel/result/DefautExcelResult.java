package com.auxdemo.adp.commons.group.excel.result;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

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
            return errorCount == 0 ? StrUtil.format("恭喜您，全部读取成功！共{}条", new Object[]{successCount}) : "";
        }
    }

    public DefautExcelResult(List<T> list, List<String> errorList) {
        this.list = list;
        this.errorList = errorList;
    }

    public DefautExcelResult() {
    }
}
