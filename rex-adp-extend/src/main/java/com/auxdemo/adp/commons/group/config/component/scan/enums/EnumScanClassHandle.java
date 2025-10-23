package com.auxdemo.adp.commons.group.config.component.scan.enums;

import com.auxdemo.adp.commons.group.config.component.ScanClassHandle;
import com.auxdemo.adp.commons.group.config.enums.EnumStaticMap;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.Set;

@Slf4j
public class EnumScanClassHandle implements ScanClassHandle {
    private ApplicationContext applicationContext;

    public EnumScanClassHandle(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void handle(BeanDefinitionRegistry registry, Set<BeanDefinition> allCandidates) {
        if (CollectionUtils.isEmpty(allCandidates)) {
            return;
        }

        for (BeanDefinition candidate : allCandidates) {
            String beanClassName = candidate.getBeanClassName();
            if (beanClassName == null) {
                log.warn("发现一个 BeanDefinition 的 beanClassName 为 null，跳过处理");
                continue;
            }

            try {
                Class<?> clazz = Class.forName(beanClassName);
                if (clazz.isEnum()) {
                    EnumStaticMap.add(clazz, this.applicationContext);
                }
            } catch (ClassNotFoundException e) {
                log.error("EnumScanClassHandle 实例扫描异常，无法找到类: {}", beanClassName, e);
            } catch (Exception | Error e) {
                log.error("EnumScanClassHandle 处理类时发生未知错误，类名: {}", beanClassName, e);
            }
        }
    }
}
