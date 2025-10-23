package com.auxdemo.adp.commons.group.excel.strategy;

import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.merge.AbstractMergeStrategy;
import com.auxdemo.adp.commons.group.excel.annotation.CellMerge;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class CellMergeStrategy extends AbstractMergeStrategy {
    private List<?> list;
    private boolean hasTitle;

    @Override
    protected void merge(Sheet sheet, Cell cell, Head head, Integer relativeRowIndex) {
        List<CellRangeAddress> cellList = handle(this.list, this.hasTitle);
        if (!CollectionUtils.isEmpty(cellList)) {
            if (cell.getRowIndex() == 1 && cell.getColumnIndex() == 0) {
                for (CellRangeAddress item : cellList) {
                    sheet.addMergedRegion(item);
                }
            }
        }
    }

    private static List<CellRangeAddress> handle(List<?> list, boolean hasTitle) {
        try {
            List<CellRangeAddress> cellList = new ArrayList<>();
            if (CollectionUtils.isEmpty(list)) {
                return cellList;
            }
            Class<?> clazz = list.get(0).getClass();
            Field[] fields = clazz.getDeclaredFields();
            List<Field> mergeFields = new ArrayList<>();
            List<Integer> mergeFieldsIndex = new ArrayList<>();

            int rowIndex = hasTitle ? 1 : 0;

            Map<Field, Method> methodCache = new HashMap<>();

            for (int i = 0; i < fields.length; i++) {
                Field field = fields[i];
                if (field.isAnnotationPresent(CellMerge.class)) {
                    CellMerge cm = field.getAnnotation(CellMerge.class);
                    mergeFields.add(field);
                    mergeFieldsIndex.add(cm.index() == -1 ? i : cm.index());

                    String name = field.getName();
                    String methodName = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                    Method method = clazz.getMethod(methodName);
                    methodCache.put(field, method);
                }
            }

            Map<Field, RepeatCell> map = new HashMap<>();

            for (int i = 0; i < list.size(); i++) {
                for (int j = 0; j < mergeFields.size(); j++) {
                    Field field = mergeFields.get(j);
                    Method readMethod = methodCache.get(field);
                    Object val = readMethod.invoke(list.get(i));
                    int colNum = mergeFieldsIndex.get(j);

                    if (!map.containsKey(field)) {
                        map.put(field, new RepeatCell(val, i));
                        continue;
                    }

                    RepeatCell repeatCell = map.get(field);
                    Object cellValue = repeatCell.getValue();
                    if (cellValue == null || "".equals(cellValue)) {
                        continue;
                    }
                    if (!Objects.equals(cellValue, val)) {
                        if (i - repeatCell.getCurrent() > 1) {
                            cellList.add(new CellRangeAddress(
                                    repeatCell.getCurrent() + rowIndex,
                                    i + rowIndex - 1,
                                    colNum,
                                    colNum
                            ));
                        }
                        map.put(field, new RepeatCell(val, i));
                    } else if (i == list.size() - 1 && i > repeatCell.getCurrent()) {
                        cellList.add(new CellRangeAddress(
                                repeatCell.getCurrent() + rowIndex,
                                i + rowIndex,
                                colNum,
                                colNum
                        ));
                    }
                }
            }

            return cellList;
        } catch (Exception e) {
            log.error("合并单元格时发生错误", e);
            throw new RuntimeException("合并单元格失败", e);
        }
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
