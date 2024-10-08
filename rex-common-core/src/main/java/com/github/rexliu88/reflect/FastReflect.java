package com.github.rexliu88.reflect;

import com.github.rexliu88.reflect.container.FastField;
import com.github.rexliu88.reflect.container.FastType;
import com.github.rexliu88.reflect.container.FastTypeContainer;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 快速反射
 */
public class FastReflect {
    public static Object get(Object obj, String namePath) throws IllegalAccessException, NoSuchFieldException {
        String[] names = namePath.split("\\.");
        for (String name : names) {
            if (obj instanceof List) {
                int index = Integer.parseInt(name);
                obj = ((List<?>) obj).get(index);
            } else {
                obj = getOnce(obj, name);
            }
        }
        return obj;
    }

    @SuppressWarnings("unchecked")
    private static <T, S extends T> Object getOnce(T obj, String name) throws IllegalAccessException, NoSuchFieldException {
        //因为子类和父类需要分别调用各自自己的代理类，所以这里需要类型强转
        Class<S> clazz = (Class<S>) obj.getClass();
        FastType<S> fastType = FastTypeContainer.getInstance().get(clazz);
        return fastType.fieldGet((S) obj, name);
    }

    public static void set(Object obj, String namePath, Object value) throws IllegalAccessException, NoSuchFieldException {
        String[] names = namePath.split("\\.");
        for (int i = 0; i < names.length - 1; i++) {
            String name = names[i];
            if (obj instanceof List) {
                int index = Integer.parseInt(name);
                obj = ((List<?>) obj).get(index);
            } else {
                obj = getOnce(obj, name);
            }
        }
        setOnce(obj, names[names.length - 1], value);
    }

    @SuppressWarnings("unchecked")
    private static <T, S extends T> void setOnce(T obj, String name, Object value) throws IllegalAccessException, NoSuchFieldException {
        //因为子类和父类需要分别调用各自自己的代理类，所以这里需要类型强转
        Class<S> clazz = (Class<S>) obj.getClass();
        FastType<S> fastType = FastTypeContainer.getInstance().get(clazz);
        fastType.fieldSet((S) obj, name, value);
    }

    @SuppressWarnings("unchecked")
    public static <T, S extends T> Object run(T obj, String name, Object... params) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //因为子类和父类需要分别调用各自自己的代理类，所以这里需要类型强转
        Class<S> clazz = (Class<S>) obj.getClass();
        FastType<S> fastType = FastTypeContainer.getInstance().get(clazz);
        return fastType.methodRun((S) obj, name, params);
    }

    public static <T> T newInstance(Class<T> clazz) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        FastType<T> fastType = FastTypeContainer.getInstance().get(clazz);
        return fastType.newInstance();
    }

    public static <T> Map<String, FastField> getFieldMap(Class<T> clazz) {
        FastType<T> fastType = FastTypeContainer.getInstance().get(clazz);
        return fastType.getFieldMap();
    }

    public static <T> Map<String, Method> getMethodMap(Class<T> clazz) {
        FastType<T> fastType = FastTypeContainer.getInstance().get(clazz);
        return fastType.getMethodMap();
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz) throws NoSuchMethodException {
        FastType<T> fastType = FastTypeContainer.getInstance().get(clazz);
        return fastType.getConstructor();
    }
}
