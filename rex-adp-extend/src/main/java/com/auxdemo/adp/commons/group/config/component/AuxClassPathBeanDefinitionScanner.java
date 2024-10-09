package com.auxdemo.adp.commons.group.config.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.TypeFilter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class AuxClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {
    private static final Logger log = LoggerFactory.getLogger(AuxClassPathBeanDefinitionScanner.class);
    private ScanClassHandle scanClassHandle;
    public AuxClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }
    public AuxClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, TypeFilter... filters) {
        super(registry, false);
        if (filters != null && filters.length > 0) {
            TypeFilter[] var3 = filters;
            int var4 = filters.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                TypeFilter f = var3[var5];
                super.addIncludeFilter(f);
            }
        }

    }
    @Override
    protected Set<BeanDefinitionHolder> doScan(String[] basePackages) {
        if (this.scanClassHandle == null) {
            throw new RuntimeException("AuxClassPathBeanDefinitionScanner未注入ScanClassHandle实现类，无法执行处理逻辑");
        } else {
            Set<BeanDefinition> allCandidates = new HashSet();
            String[] var3 = basePackages;
            int var4 = basePackages.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String basePackage = var3[var5];
                Set<BeanDefinition> candidates = super.findCandidateComponents(basePackage);
                allCandidates.addAll(candidates);
            }

            this.scanClassHandle.handle(super.getRegistry(), allCandidates);
            return new LinkedHashSet();
        }
    }
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return true;
    }
    public void setScanClassHandle(ScanClassHandle scanClassHandle) {
        this.scanClassHandle = scanClassHandle;
    }
}

