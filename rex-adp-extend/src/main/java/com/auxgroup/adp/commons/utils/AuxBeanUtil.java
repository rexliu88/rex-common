package com.auxgroup.adp.commons.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AuxBeanUtil {

    public static <T, V> V copy(T source, Class<V> desc) {
        if (ObjectUtil.isNull(source)) {
            return null;
        }
        if (ObjectUtil.isNull(desc)) {
            return null;
        }
        V target = ReflectUtil.newInstance(desc);
        BeanUtil.copyProperties(source,target);
        return target;
    }

    public static <T, V> List<V> copyList(Collection<T> sourceList, Class<V> desc) {
        List<V> targetList = new ArrayList<>();
        if(CollUtil.isEmpty(sourceList)) {
            return targetList;
        }
        for (T source : sourceList) {
            if(ObjectUtil.isNull(source)) {
                continue;
            }
            V target = copy(source,desc);
            if(ObjectUtil.isNotNull(target)) {
                targetList.add(target);
            }
        }
        return targetList;
    }
}
