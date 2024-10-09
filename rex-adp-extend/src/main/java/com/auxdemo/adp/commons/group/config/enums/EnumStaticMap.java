package com.auxdemo.adp.commons.group.config.enums;


import cn.hutool.core.util.StrUtil;
import com.auxdemo.adp.commons.group.config.component.scan.enums.AuxEnumIgnoreProps;
import com.auxgroup.adp.commons.group.config.enums.AuxEnum;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.*;
public class EnumStaticMap {
    private static final Logger log = LoggerFactory.getLogger(EnumStaticMap.class);
    private static final Map<String, List<AuxEnum.AuxEnumProps>> map = new HashMap();
    private static final Map<String, String> clazzMap = new HashMap();
    private static AuxEnumIgnoreProps props;
    public static void add(Class clazz, ApplicationContext applicationContext) {
        if (props == null) {
            Class var2 = EnumStaticMap.class;
            synchronized(EnumStaticMap.class) {
                props = (AuxEnumIgnoreProps)applicationContext.getBean(AuxEnumIgnoreProps.class);
            }
        }

        List<String> ignoreClassList = props.getIgnoreClassList(clazz.getSimpleName());
        if (CollectionUtils.isEmpty(ignoreClassList) || !ignoreClassList.contains(clazz.getName())) {
            if (map.containsKey(clazz.getSimpleName())) {
                log.warn("【枚举解析警告】" + clazz.getName() + "对应的simpleName存在于MAP中,冲突类：" + StrUtil.nullToEmpty((String)clazzMap.get(clazz.getSimpleName())));
                log.warn("您可以指定冲突类的某一个class忽略解析，从而消除本次异常，请在配置文件中配置IGNORE，配置如下：");
                log.warn("==========================================");
                log.warn("aux:");
                log.warn("  enums:");
                log.warn("    conflict:");
                log.warn("      ignores: xxx.xx.xx.xx,xx.xx.xx.xx(忽略多个类可以逗号分隔)");
                log.warn("==========================================");
                throw new RuntimeException("【枚举解析冲突】" + clazz.getName() + "对应的simpleName存在于MAP中,冲突类：" + StrUtil.nullToEmpty((String)clazzMap.get(clazz.getSimpleName())));
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
        } else if ("$$$ALL$$$".equals(types.get(0))) {
            return map;
        } else {
            Map<String, List<AuxEnum.AuxEnumProps>> rsMap = new HashMap();
            Iterator var2 = types.iterator();

            while(var2.hasNext()) {
                String type = (String)var2.next();
                if (map.containsKey(type)) {
                    rsMap.put(type, map.get(type));
                }
            }

            return rsMap;
        }
    }

    private static Object invokeMethod(Object instance, Method method) {
        try {
            return method.invoke(instance);
        } catch (Exception var3) {
            return null;
        }
    }

    private static Method getMethod(Class clazz, String methodName) {
        try {
            Method m = clazz.getMethod(methodName);
            return m;
        } catch (Exception var3) {
            return null;
        }
    }

    public static <T> List<AuxEnum.AuxEnumProps> parseEnumToList(Class<T> enumType) {
        try {
            Method codeFieldMethod = getMethod(enumType, "getCodeField");
            Method nameFieldMethod = getMethod(enumType, "getNameField");
            Method otherPropsKeysMethod = getMethod(enumType, "getOtherPropsKeys");
            if (codeFieldMethod != null && nameFieldMethod != null) {
                T[] values = enumType.getEnumConstants();
                PropertyUtilsBean propertyUtilsBean = new PropertyUtilsBean();
                List<AuxEnum.AuxEnumProps> list = new ArrayList();
                String codeField = null;
                String nameField = null;
                String[] otherPropsKeys = null;
                Object[] var10 = values;
                int var11 = values.length;

                for(int var12 = 0; var12 < var11; ++var12) {
                    T v = (T)var10[var12];
                    if (codeField == null) {
                        codeField = (String)invokeMethod(v, codeFieldMethod);
                    }

                    if (nameField == null) {
                        nameField = (String)invokeMethod(v, nameFieldMethod);
                    }

                    if (otherPropsKeys == null) {
                        otherPropsKeys = (String[])((String[])invokeMethod(v, otherPropsKeysMethod));
                    }

                    Object code = propertyUtilsBean.getNestedProperty(v, codeField);
                    Object name = propertyUtilsBean.getNestedProperty(v, nameField);
                    if (code != null && name != null) {
                        Map<String, Object> otherPropsMap = new HashMap();
                        if (otherPropsKeys != null && otherPropsKeys.length > 0) {
                            String[] var17 = otherPropsKeys;
                            int var18 = otherPropsKeys.length;

                            for(int var19 = 0; var19 < var18; ++var19) {
                                String otherPropsKey = var17[var19];
                                Object otherPropsValue = propertyUtilsBean.getNestedProperty(v, otherPropsKey);
                                if (otherPropsValue != null) {
                                    otherPropsMap.put(otherPropsKey, otherPropsValue);
                                }
                            }
                        }

                        list.add(new AuxEnum.AuxEnumProps(code, name, otherPropsMap));
                    }
                }

                return list;
            } else {
                return null;
            }
        } catch (NoSuchMethodException var22) {
            log.error("枚举类解析异常 如果您集成了AuxEnum接口，请用code,name属性定义枚举，若确实要自定义，请实现getCodeField和getNameField自定义映射code和name", var22);
            return null;
        } catch (Exception var23) {
            log.error("枚举类解析异常", var23);
            return null;
        }
    }
}
