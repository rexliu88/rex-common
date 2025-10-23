
package com.auxdemo.adp.commons.group.excel.convert;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import com.auxdemo.adp.commons.group.excel.annotation.ExcelDictFormat;
import com.auxgroup.adp.commons.group.excel.AuxExcelUtil;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;

@Slf4j
public class ExcelDictConvert implements Converter<Object> {
    @Override
    public Class<Object> supportJavaTypeKey() {
        return Object.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return null;
    }

    @Override
    public Object convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        ExcelDictFormat anno = getAnnotationValidated(contentProperty.getField());
        String label = cellData.getStringValue();
        try {
            String value = AuxExcelUtil.reverseByExp(label, anno.readConverterExp(), anno.separator());
            return Convert.convert(contentProperty.getField().getType(), value);
        } catch (Exception e) {
            log.error("Excel字典反向转换失败: label={}, exp={}, separator={}", label, anno.readConverterExp(), anno.separator(), e);
            throw new RuntimeException("Excel字典反向转换失败", e);
        }
    }

    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isNull(object)) {
            return new WriteCellData<>("");
        } else {
            ExcelDictFormat anno = getAnnotationValidated(contentProperty.getField());
            String value = Convert.toStr(object);
            try {
                String label = AuxExcelUtil.convertByExp(value, anno.readConverterExp(), anno.separator());
                return new WriteCellData<>(label);
            } catch (Exception e) {
                log.error("Excel字典正向转换失败: value={}, exp={}, separator={}", value, anno.readConverterExp(), anno.separator(), e);
                throw new RuntimeException("Excel字典正向转换失败", e);
            }
        }
    }

    private ExcelDictFormat getAnnotation(Field field) {
        return (ExcelDictFormat) AnnotationUtil.getAnnotation(field, ExcelDictFormat.class);
    }

    private ExcelDictFormat getAnnotationValidated(Field field) {
        ExcelDictFormat anno = getAnnotation(field);
        if (anno == null) {
            String errorMsg = "字段 [" + field.getName() + "] 缺少 @ExcelDictFormat 注解";
            log.error(errorMsg);
            throw new IllegalArgumentException(errorMsg);
        }
        return anno;
    }
}