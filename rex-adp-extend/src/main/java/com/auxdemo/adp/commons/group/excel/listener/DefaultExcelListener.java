package com.auxdemo.adp.commons.group.excel.listener;

import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.exception.ExcelAnalysisException;
import com.alibaba.excel.exception.ExcelDataConvertException;
import com.alibaba.fastjson2.JSON;
import com.auxdemo.adp.commons.group.excel.result.DefautExcelResult;
import com.auxdemo.adp.commons.group.excel.result.ExcelResult;
import com.auxdemo.adp.commons.group.utils.AuxValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class DefaultExcelListener<T> extends AnalysisEventListener<T> implements ExcelListener<T> {
    private static final Logger log = LoggerFactory.getLogger(DefaultExcelListener.class);
    private Boolean isValidate;
    private Map<Integer, String> headMap;
    private ExcelResult<T> excelResult;

    public DefaultExcelListener(boolean isValidate) {
        this.excelResult = new DefautExcelResult();
        this.isValidate = isValidate;
    }

    @Override
    public void onException(Exception exception, AnalysisContext context) throws Exception {
        String errMsg = null;
        if (exception instanceof ExcelDataConvertException) {
            ExcelDataConvertException excelDataConvertException = (ExcelDataConvertException)exception;
            Integer rowIndex = excelDataConvertException.getRowIndex();
            Integer columnIndex = excelDataConvertException.getColumnIndex();
            errMsg = StrUtil.format("第{}行-第{}列-表头{}: 解析异常<br/>", new Object[]{rowIndex + 1, columnIndex + 1, this.headMap.get(columnIndex)});
            if (log.isDebugEnabled()) {
                log.error(errMsg);
            }
        }

        if (exception instanceof ConstraintViolationException) {
            ConstraintViolationException constraintViolationException = (ConstraintViolationException)exception;
            Set<ConstraintViolation<?>> constraintViolations = constraintViolationException.getConstraintViolations();
            String constraintViolationsMsg = (String)constraintViolations.stream().map(ConstraintViolation::getMessage).filter(Objects::nonNull).collect(Collectors.joining(","));
            errMsg = StrUtil.format("第{}行数据校验异常: {}", new Object[]{context.readRowHolder().getRowIndex() + 1, constraintViolationsMsg});
            if (log.isDebugEnabled()) {
                log.error(errMsg);
            }
        }

        this.excelResult.getErrorList().add(errMsg);
        throw new ExcelAnalysisException(errMsg);
    }

    @Override
    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        this.headMap = headMap;
        log.debug("解析到一条表头数据: {}", JSON.toJSONString(headMap));
    }

    @Override
    public void invoke(T data, AnalysisContext context) {
        if (this.isValidate) {
            AuxValidatorUtil.validate(data);
        }

        this.excelResult.getList().add(data);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.debug("所有数据解析完成！");
    }

    @Override
    public ExcelResult<T> getExcelResult() {
        return this.excelResult;
    }

    public DefaultExcelListener() {
        this.isValidate = Boolean.TRUE;
    }
}
