
package com.auxdemo.adp.commons.group.excel.convert;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.data.ReadCellData;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Slf4j
public class ExcelBigNumberConvert implements Converter<Long> {

    @Override
    public Class<Long> supportJavaTypeKey() {
        return Long.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public Long convertToJavaData(ReadCellData<?> cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        return Convert.toLong(cellData.getData());
    }

    @Override
    public WriteCellData<?> convertToExcelData(Long object, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) {
        if (ObjectUtil.isNull(object)) {
            return new WriteCellData<>((BigDecimal) null);
        }

        String str = Convert.toStr(object);
        if (str.length() > 15) {
            return new WriteCellData<>(str);
        }

        // 使用 valueOf 避免 double 精度损失
        WriteCellData<BigDecimal> cellData = new WriteCellData<>(BigDecimal.valueOf(object));
        cellData.setType(CellDataTypeEnum.NUMBER);
        return cellData;
    }
}