package com.github.rexliu88.reflect.container;


import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 代理类，用于缓存某个类的各种反射结果
 */
public class FastType<T> {
    //类型
    private final Class<T> clazz;
    //字段名 -> 字段 Map
    private volatile Map<String, FastField> fieldMap;
    //方法名 -> 方法 Map
    private volatile Map<String, Method> methodMap;
    //构造函数
    private volatile Constructor<T> constructor;
    //表名
    //唯一索引主键字段
    //抓取字段
    //转换字段

    public FastType(Class<T> clazz) {
        this.clazz = clazz;
    }

    public Map<String, FastField> getFieldMap() {
        if (fieldMap == null) {
            synchronized (this) {
                if (fieldMap == null) {
                    //懒加载field，一次性加载所有field
                    Field[] fields = clazz.getDeclaredFields();
                    Map<String, FastField> tempFieldMap = new HashMap<>((int) (fields.length / 0.75f + 1f));
                    for (Field cur : fields) {
                        cur.setAccessible(true);
                        tempFieldMap.put(cur.getName(), new FastField(cur));
                    }
                    fieldMap = tempFieldMap;
                }
            }
        }
        return fieldMap;
    }

    public Map<String, Method> getMethodMap() {
        if (methodMap == null) {
            synchronized (this) {
                if (methodMap == null) {
                    //懒加载method，一次性加载所有method
                    Method[] methods = clazz.getDeclaredMethods();
                    Map<String, Method> tempMethodMap = new HashMap<>((int) (methods.length / 0.75f + 1f));
                    for (Method cur : methods) {
                        cur.setAccessible(true);
                        tempMethodMap.put(cur.getName(), cur);
                    }
                    methodMap = tempMethodMap;
                }
            }
        }
        return methodMap;
    }

    public Constructor<T> getConstructor() throws NoSuchMethodException {
        if (constructor == null) {
            synchronized (this) {
                if (constructor == null) {
                    //懒加载constructor
                    Constructor<T> c = clazz.getConstructor();
                    c.setAccessible(true);
                    constructor = c;
                }
            }
        }
        return constructor;
    }

    /**
     * 获得当前FastType某个对应实例的某个字段的值
     */
    public Object fieldGet(T obj, String name) throws IllegalAccessException, NoSuchFieldException {
        Map<String, FastField> map = getFieldMap();
        FastField field = map.get(name);
        if (field == null) {
            throw new NoSuchFieldException(clazz.getName() + "." + name);
        }
        return field.getField().get(obj);
    }

    /**
     * 设置当前FastType某个对应实例的某个字段的值
     */
    public void fieldSet(T obj, String name, Object value) throws IllegalAccessException, NoSuchFieldException {
        Map<String, FastField> map = getFieldMap();
        FastField field = map.get(name);
        if (field == null) {
            throw new NoSuchFieldException(clazz.getName() + "." + name);
        }
        //安全的set，自动转化类型
        Class<?> fieldClazz = field.getField().getType();
        if (!fieldClazz.isAssignableFrom(value.getClass())) {
            Object castedValue = JSONObject.parseObject(value.toString(), fieldClazz);
            field.getField().set(obj, castedValue);
        } else {
            field.getField().set(obj, value);
        }

    }

    /**
     * 执行当前FastType某个对应实例的某个方法
     */
    public Object methodRun(T obj, String name, Object... params) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Map<String, Method> map = getMethodMap();
        Method method = map.get(name);
        if (method == null) {
            throw new NoSuchMethodException(clazz.getName() + "." + name);
        }
        return method.invoke(obj, params);
    }

    /**
     * 新建当前FastType的一个实例
     */
    public T newInstance() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<T> c = getConstructor();
        return c.newInstance();
    }
}
