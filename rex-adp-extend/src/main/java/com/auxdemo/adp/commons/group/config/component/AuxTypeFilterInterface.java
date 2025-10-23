package com.auxdemo.adp.commons.group.config.component;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import java.io.IOException;

/**
 * 自定义类型过滤器，用于匹配实现指定接口的类
 * 该过滤器继承自AssignableTypeFilter，重写了匹配逻辑，专门用于检查类是否实现了目标接口
 */
public class AuxTypeFilterInterface extends AssignableTypeFilter {
    /**
     * 构造函数，初始化目标类型过滤器
     * @param targetType 目标类型，用于后续的类型匹配检查
     */
    public AuxTypeFilterInterface(Class<?> targetType) {
        super(targetType);
    }

    /**
     * 重写的匹配方法，检查元数据中表示的类是否实现了目标接口
     * @param metadataReader 当前类的元数据读取器
     * @param metadataReaderFactory 元数据读取器工厂
     * @return 如果当前类实现了目标接口则返回true，否则返回false
     * @throws IOException IO异常
     */
    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        // 获取类元数据并防止空指针
        if (metadataReader == null || metadataReader.getClassMetadata() == null) {
            return false;
        }
        // 获取类所实现的所有接口名
        String[] interfaceNames = metadataReader.getClassMetadata().getInterfaceNames();
        if (interfaceNames == null || interfaceNames.length == 0 ) {
            return false;
        }
        // 获取目标接口全限定名
        Class<?> targetType = super.getTargetType();
        if (targetType == null) {
            return false;
        }

        String targetTypeName = targetType.getName();  // 缓存目标类型名称

        // 检查是否有接口匹配目标类型
        for (String interfaceName : interfaceNames) {
            if (targetTypeName.equals(interfaceName)) {
                return true;
            }
        }

        return false;
    }
}
