package com.github.rexliu88.easydb.model;

import cn.hutool.core.util.StrUtil;
import com.github.rexliu88.common.ResEntity;
import com.github.rexliu88.common.User;
import com.github.rexliu88.easydb.model.ColumnData;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 表数据对象
 */
@Data
@NoArgsConstructor
public class TableData {
    /**
     * 当前操作用户
     */
    private User curUser;
    /**
     * 表表名
     */
    public String tableName;
    /**
     * 表字段数据对象列表
     */
    List<ColumnData> columnDataList;
    /**
     * 表主键字段 支持多个组合主键 只是在更新时使用，并且 列在 columnDataList 同时存在。
     */
    List<ColumnData> primaryKeyColumnDataList;

    public TableData(String tableName){
        this.tableName = tableName;
        this.columnDataList = new ArrayList<>();
        this.primaryKeyColumnDataList = new ArrayList<>();
    }
    public TableData(String tableName,User curUser){
        this(tableName);
        this.curUser = curUser;
    }
    public TableData(String tableName,String primaryKeyColumnName,Object primaryKeyColumnValue,User curUser){
        this(tableName);
        this.curUser = curUser;
        addPrimaryKeyColumn(primaryKeyColumnName,primaryKeyColumnValue);
    }
    public void addColumn(String columnName,Object columnValue){
        ColumnData columnData = new ColumnData(columnName, columnValue);
        addColumn(columnData);
    }
    public void addColumn(ColumnData columnData) {
        columnDataList.add(columnData);
    }
    public void addPrimaryKeyColumn(String primaryKeyColumnName,Object primaryKeyColumnValue){
        ColumnData primaryKeyColumnData = new ColumnData(primaryKeyColumnName, primaryKeyColumnValue);
        addPrimaryKeyColumn(primaryKeyColumnData);
    }
    public void addPrimaryKeyColumn(ColumnData primaryKeyColumnData) {
        primaryKeyColumnDataList.add(primaryKeyColumnData);
        columnDataList.add(primaryKeyColumnData);
    }
    public void preInsert() {
        columnDataList.add(new ColumnData(ResEntity.ADD_TIME,new Date()));
        columnDataList.add(new ColumnData(ResEntity.UPDATE_TIME,new Date()));
        if(curUser!=null && StrUtil.isAllNotBlank(curUser.getName())) {
            columnDataList.add(new ColumnData(ResEntity.ADD_USER, curUser.getName()));
            columnDataList.add(new ColumnData(ResEntity.UPDATE_USER,curUser.getName()));
        }
        //columnDataList.add(ResDalUtil.makeColumnData("deleted",0));
    }
    public void preUpdate() {
        columnDataList.add(new ColumnData(ResEntity.UPDATE_TIME,new Date()));
        if(curUser!=null && StrUtil.isAllNotBlank(curUser.getName())) {
            columnDataList.add(new ColumnData(ResEntity.UPDATE_USER, curUser.getName()));
        }
    }
}
