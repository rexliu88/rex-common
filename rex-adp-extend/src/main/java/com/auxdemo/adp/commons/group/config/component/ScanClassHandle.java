package com.auxdemo.adp.commons.group.config.component;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Set;

public interface ScanClassHandle {
    void handle(BeanDefinitionRegistry var1, Set<BeanDefinition> var2);
}
