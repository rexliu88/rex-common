package com.github.rexliu88.reflect.container;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 代理类的单例容器，子类和父类将创建各自的代理类
 */
public class FastTypeContainer {
    //region 单例模式

    private volatile static FastTypeContainer instance = null;

    public static FastTypeContainer getInstance() {
        if (instance == null) {
            synchronized (FastTypeContainer.class) {
                if (instance == null) {
                    //volatile保证整个构建过程不被重排，对instance==null是可见的
                    instance = new FastTypeContainer();
                }
            }
        }
        return instance;
    }

    private FastTypeContainer() {
        this.map = new ConcurrentHashMap<>();
    }

    private final ConcurrentMap<Class<?>, FastType<?>> map;
    //endregion

    /**
     * 根据Class获得它对应的FastType
     */
    @SuppressWarnings("unchecked")
    public <T> FastType<T> get(Class<T> clazz) {
        FastType<?> fastType = this.map.get(clazz);
        if (fastType == null) {
            //懒加载clazz对应的fastType
            fastType = new FastType<>(clazz);
            //通过检查putIfAbsent的返回值，保证fastType永远是同一个
            FastType<?> tempFastType = this.map.putIfAbsent(clazz, fastType);
            if (tempFastType != null) {
                fastType = tempFastType;
            }
        }
        //因为this.map没有强制key和value的泛型一致，所以这里需要类型强转
        return (FastType<T>) fastType;
    }


}
