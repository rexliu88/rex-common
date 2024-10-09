package com.auxdemo.adp.commons.group.excel.strategy;


import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.auxdemo.adp.commons.group.excel.annotation.CellMerge;
import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class CellMergeStrategy extends AbstractMergeStrategy {
    private static final Logger log = LoggerFactory.getLogger(CellMergeStrategy.class);
    private List<?> list;
    private boolean hasTitle;

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        List<CellRangeAddress> cellList = handle(this.list, this.hasTitle);
        if (!CollectionUtils.isEmpty(cellList)) {
            if (cell.getRowIndex() == 1 && cell.getColumnIndex() == 0) {
                Iterator var6 = cellList.iterator();

                while(var6.hasNext()) {
                    CellRangeAddress item = (CellRangeAddress)var6.next();
                    sheet.addMergedRegion(item);
                }
            }

        }
    }

    private static List<CellRangeAddress> handle(List<?> list, boolean hasTitle) {
        try {
            List<CellRangeAddress> cellList = new ArrayList();
            if (CollectionUtils.isEmpty(list)) {
                return cellList;
            } else {
                Class<?> clazz = list.get(0).getClass();
                Field[] fields = clazz.getDeclaredFields();
                List<Field> mergeFields = new ArrayList();
                List<Integer> mergeFieldsIndex = new ArrayList();

                int rowIndex;
                for(rowIndex = 0; rowIndex < fields.length; ++rowIndex) {
                    Field field = fields[rowIndex];
                    if (field.isAnnotationPresent(CellMerge.class)) {
                        CellMerge cm = (CellMerge)field.getAnnotation(CellMerge.class);
                        mergeFields.add(field);
                        mergeFieldsIndex.add(cm.index() == -1 ? rowIndex : cm.index());
                    }
                }

                rowIndex = hasTitle ? 1 : 0;
                Map<Field, RepeatCell> map = new HashMap();

                for(int i = 0; i < list.size(); ++i) {
                    for(int j = 0; j < mergeFields.size(); ++j) {
                        Field field = (Field)mergeFields.get(j);
                        String name = field.getName();
                        String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                        Method readMethod = clazz.getMethod(methodName);
                        Object val = readMethod.invoke(list.get(i));
                        int colNum = (Integer)mergeFieldsIndex.get(j);
                        if (!map.containsKey(field)) {
                            map.put(field, new RepeatCell(val, i));
                        } else {
                            RepeatCell repeatCell = (RepeatCell)map.get(field);
                            Object cellValue = repeatCell.getValue();
                            if (cellValue != null && !"".equals(cellValue)) {
                                if (cellValue != val) {
                                    if (i - repeatCell.getCurrent() > 1) {
                                        cellList.add(new CellRangeAddress(repeatCell.getCurrent() + rowIndex, i + rowIndex - 1, colNum, colNum));
                                    }

                                    map.put(field, new RepeatCell(val, i));
                                } else if (i == list.size() - 1 && i > repeatCell.getCurrent()) {
                                    cellList.add(new CellRangeAddress(repeatCell.getCurrent() + rowIndex, i + rowIndex, colNum, colNum));
                                }
                            }
                        }
                    }
                }

                return cellList;
            }
        } catch (Throwable var19) {
            //throw var19;
            //todo 报错
        }
        return null;
    }

    public CellMergeStrategy(List<?> list, boolean hasTitle) {
        this.list = list;
        this.hasTitle = hasTitle;
    }

    @Data
    static class RepeatCell {
        private Object value;
        private int current;

        public RepeatCell(Object value, int current) {
            this.value = value;
            this.current = current;
        }
    }
}
