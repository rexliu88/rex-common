package com.auxgroup.adp.commons.utils.page;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.auxdemo.adp.commons.utils.AuxSqlUtil;

@Data
public class PageQuery implements Serializable {
    private Integer pageSize;
    private Integer pageNum;
    private String orderByColumn;
    private String isAsc;
    public static final int DEFAULT_PAGE_NUM = 1;
    public static final int DEFAULT_PAGE_SIZE = 10;

    public <T> Page<T> build() {
        Integer pageNum = (Integer) ObjectUtil.defaultIfNull(this.getPageNum(), 1);
        Integer pageSize = (Integer)ObjectUtil.defaultIfNull(this.getPageSize(), 10);
        if (pageNum <= 0) {
            pageNum = 1;
        }

        Page<T> page = new Page((long)pageNum, (long)pageSize);
        List<OrderItem> orderItems = this.buildOrderItem();
        if (CollUtil.isNotEmpty(orderItems)) {
            page.addOrder(orderItems);
        }

        return page;
    }

    private List<OrderItem> buildOrderItem() {
        if (!StrUtil.isBlank(this.orderByColumn) && !StrUtil.isBlank(this.isAsc)) {
            String orderBy = AuxSqlUtil.escapeOrderBySql(this.orderByColumn);
            orderBy = StrUtil.toUnderlineCase(orderBy);
            this.isAsc = cn.hutool.core.util.StrUtil.replace(this.isAsc, "ascending", "asc");
            this.isAsc = cn.hutool.core.util.StrUtil.replace(this.isAsc, "descending", "desc");
            String[] orderByArr = orderBy.split(",");
            String[] isAscArr = this.isAsc.split(",");
            if (isAscArr.length != 1 && isAscArr.length != orderByArr.length) {
                throw new RuntimeException("排序参数有误");
            } else {
                List<OrderItem> list = new ArrayList();

                for(int i = 0; i < orderByArr.length; ++i) {
                    String orderByStr = orderByArr[i];
                    String isAscStr = isAscArr.length == 1 ? isAscArr[0] : isAscArr[i];
                    if ("asc".equals(isAscStr)) {
                        list.add(OrderItem.asc(orderByStr));
                    } else {
                        if (!"desc".equals(isAscStr)) {
                            throw new RuntimeException("排序参数有误");
                        }

                        list.add(OrderItem.desc(orderByStr));
                    }
                }

                return list;
            }
        } else {
            return null;
        }
    }
}
