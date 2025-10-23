package com.auxdemo.adp.commons.group.config.enums;


import cn.hutool.core.util.StrUtil;
import com.auxdemo.adp.commons.group.config.component.scan.enums.AuxEnumIgnoreProps;
import com.auxgroup.adp.commons.group.config.enums.AuxEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class EnumStaticMap {
    // 修改为线程安全的 ConcurrentHashMap
    private static final Map<String, List<AuxEnum.AuxEnumProps>> map = new ConcurrentHashMap();
    private static final Map<String, String> clazzMap = new ConcurrentHashMap();
    private static AuxEnumIgnoreProps props;

    private static final String ALL_TYPES_FLAG = "$$$ALL$$$";
    public static void add(Class clazz, ApplicationContext applicationContext) {
        if (props == null) {
            synchronized (EnumStaticMap.class) {
                if (props == null) {
                    props = applicationContext.getBean(AuxEnumIgnoreProps.class);
                }
            }
        }

        List<String> ignoreClassList = props.getIgnoreClassList(clazz.getSimpleName());
        if(!CollectionUtils.isEmpty(ignoreClassList) && ignoreClassList.contains(clazz.getName())) {
            return;
        }
        synchronized (EnumStaticMap.class) {
            if (map.containsKey(clazz.getSimpleName())) {
                String conflictClazzName = StrUtil.nullToEmpty(clazzMap.get(clazz.getSimpleName()));
                log.warn("【枚举解析警告】{}对应的simpleName存在于MAP中,冲突类：{}", clazz.getName(), conflictClazzName);
                log.warn("您可以指定冲突类的某一个class忽略解析，从而消除本次异常，请在配置文件中配置IGNORE，配置如下：");
                log.warn("==========================================");
                log.warn("aux:");
                log.warn("  enums:");
                log.warn("    conflict:");
                log.warn("      ignores: xxx.xx.xx.xx,xx.xx.xx.xx(忽略多个类可以逗号分隔)");
                log.warn("==========================================");
                throw new RuntimeException("【枚举解析冲突】" + clazz.getName() + "对应的simpleName存在于MAP中,冲突类：" + conflictClazzName);
            } else {
                List<AuxEnum.AuxEnumProps> valueList = parseEnumToList(clazz);
                if (valueList != null) {
                    map.put(clazz.getSimpleName(), valueList);
                    clazzMap.put(clazz.getSimpleName(), clazz.getName());
                }
            }
        }
    }

    public static Map<String, List<AuxEnum.AuxEnumProps>> getByTypeList(List<String> types) {
        if (CollectionUtils.isEmpty(types)) {
            return null;
        }

        if (ALL_TYPES_FLAG.equals(types.get(0))) {
            //return new HashMap<>(map); // 避免外部修改原 map
            return map;
        }

        Map<String, List<AuxEnum.AuxEnumProps>> rsMap = new HashMap<>();
        for (String type : types) {
            if (map.containsKey(type)) {
                rsMap.put(type, map.get(type));
            }
        }

        return rsMap;
    }

    private static Object invokeMethod(Object instance, Method method) {
        try {
            return method.invoke(instance);
        } catch (Exception e) {
            log.error("方法调用失败: {}", method.getName(), e);
            return null;
        }
    }

    private static Method getMethod(Class clazz, String methodName) {
        try {
            return clazz.getMethod(methodName);
        } catch (NoSuchMethodException e) {
            log.warn("找不到方法: {}", methodName, e);
            return null;
        }
    }

    public static <T> List<AuxEnum.AuxEnumProps> parseEnumToList(Class<T> enumType) {
        try {
            Method codeFieldMethod = getMethod(enumType, "getCodeField");
            Method nameFieldMethod = getMethod(enumType, "getNameField");
            Method otherPropsKeysMethod = getMethod(enumType, "getOtherPropsKeys");

            if (codeFieldMethod == null || nameFieldMethod == null) {
                // 枚举类解析异常 如果您集成了AuxEnum接口，
                // 请用code,name属性定义枚举，若确实要自定义，
                // 请实现getCodeField和getNameField自定义映射code和name
                log.warn("枚举类 {} 缺少必要方法 getCodeField: {}  或 getNameField: {} "
                        , enumType.getName()
                        ,codeFieldMethod == null
                        ,nameFieldMethod == null);
                return null;
            }

            T[] values = enumType.getEnumConstants();
            PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
            List<AuxEnum.AuxEnumProps> list = new ArrayList<>();

            String codeField = null;
            String nameField = null;
            String[] otherPropsKeys = null;

            for (T v : values) {
                if (codeField == null) {
                    codeField = (String) invokeMethod(v, codeFieldMethod);
                }
                if (nameField == null) {
                    nameField = (String) invokeMethod(v, nameFieldMethod);
                }
                if (otherPropsKeys == null && otherPropsKeysMethod != null) {
                    otherPropsKeys = (String[]) invokeMethod(v, otherPropsKeysMethod);
                }

                if (codeField == null || nameField == null) {
                    log.warn("枚举类 {} 缺少必要属性code: {} 或name: {}", enumType.getName(), codeField == null, nameField == null);
                    continue;
                }

                Object code = propertyUtilsBean.getNestedProperty(v, codeField);
                Object name = propertyUtilsBean.getNestedProperty(v, nameField);

                if (code == null || name == null) {
                    continue;
                }
                Map<String, Object> otherPropsMap = new HashMap<>();

                if (otherPropsKeys != null && otherPropsKeys.length > 0) {
                    for (String otherPropsKey : otherPropsKeys) {
                        Object otherPropsValue = propertyUtilsBean.getNestedProperty(v, otherPropsKey);
                        if (otherPropsValue != null) {
                            otherPropsMap.put(otherPropsKey, otherPropsValue);
                        }
                    }
                }

                list.add(new AuxEnum.AuxEnumProps(code, name, otherPropsMap));
            }

            return list;
        } catch (Exception e) {
            log.error("枚举类解析异常", e);
            return null;
        }
    }
}
