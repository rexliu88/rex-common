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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class ExcelDictConvert implements Converter<Object> {
    private static final Logger log = LoggerFactory.getLogger(ExcelDictConvert.class);
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
        ExcelDictFormat anno = this.getAnnotation(contentProperty.getField());
        String label = cellData.getStringValue();
        String value = AuxExcelUtil.reverseByExp(label, anno.readConverterExp(), anno.separator());
        return Convert.convert(contentProperty.getField().getType(), value);
    }
    @Override
    public WriteCellData<String> convertToExcelData(Object object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isNull(object)) {
            return new WriteCellData("");
        } else {
            ExcelDictFormat anno = this.getAnnotation(contentProperty.getField());
            String value = Convert.toStr(object);
            String label = AuxExcelUtil.convertByExp(value, anno.readConverterExp(), anno.separator());
            return new WriteCellData(label);
        }
    }
    private ExcelDictFormat getAnnotation(Field field) {
        return (ExcelDictFormat) AnnotationUtil.getAnnotation(field, ExcelDictFormat.class);
    }
}
