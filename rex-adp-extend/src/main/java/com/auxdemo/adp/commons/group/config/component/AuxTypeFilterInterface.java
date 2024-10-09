package com.auxdemo.adp.commons.group.config.component;

import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
public class AuxTypeFilterInterface extends AssignableTypeFilter {
    public AuxTypeFilterInterface(Class<?> targetType) {
        super(targetType);
    }

    @Override
    public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
        if (metadataReader.getClassMetadata().getInterfaceNames() != null && metadataReader.getClassMetadata().getInterfaceNames().length > 0) {
            List<String> interfaceList = Arrays.asList(metadataReader.getClassMetadata().getInterfaceNames());
            return interfaceList.contains(super.getTargetType().getName());
        } else {
            return false;
        }
    }
}
