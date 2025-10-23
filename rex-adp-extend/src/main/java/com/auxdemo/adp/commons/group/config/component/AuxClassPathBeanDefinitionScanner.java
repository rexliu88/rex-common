package com.auxdemo.adp.commons.group.config.component;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.TypeFilter;

import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

@Slf4j
public class AuxClassPathBeanDefinitionScanner extends ClassPathBeanDefinitionScanner {

    private ScanClassHandle scanClassHandle;

    public AuxClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
        super(registry, false);
    }

    public AuxClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, TypeFilter... filters) {
        super(registry, false);
        if (filters != null && filters.length > 0) {
            for (TypeFilter f : filters) {
                super.addIncludeFilter(f);
            }
        }

    }
    @Override
    protected Set<BeanDefinitionHolder> doScan(String[] basePackages) {
        if (this.scanClassHandle == null) {
            throw new RuntimeException("AuxClassPathBeanDefinitionScanner未注入ScanClassHandle实现类，无法执行处理逻辑");
        }

        if (basePackages == null || basePackages.length == 0) {
            log.warn("No base packages provided to scan.");
            return Collections.emptySet();
        }

        Set<BeanDefinition> allCandidates = new HashSet<>((int)(basePackages.length * 0.75f + 1));
        for (String basePackage : basePackages) {
            if (basePackage == null) continue;
            Set<BeanDefinition> candidates = super.findCandidateComponents(basePackage);
            allCandidates.addAll(candidates);
        }

        log.info("Found {} candidate components.", allCandidates.size());
        this.scanClassHandle.handle(super.getRegistry(), allCandidates);

        // 注意：此处返回空集合违反常规语义，请确保上层不依赖返回值
        return new LinkedHashSet<>();
    }
    @Override
    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return true;
    }

    public void setScanClassHandle(ScanClassHandle scanClassHandle) {
        this.scanClassHandle = scanClassHandle;
    }
}

