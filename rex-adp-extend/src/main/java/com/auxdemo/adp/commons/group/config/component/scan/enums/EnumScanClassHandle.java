package com.auxdemo.adp.commons.group.config.component.scan.enums;

import com.auxdemo.adp.commons.group.config.component.ScanClassHandle;
import com.auxdemo.adp.commons.group.config.enums.EnumStaticMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.util.CollectionUtils;

import java.util.Iterator;
import java.util.Set;

public class EnumScanClassHandle implements ScanClassHandle {
    private static final Logger log = LoggerFactory.getLogger(EnumScanClassHandle.class);
    private ApplicationContext applicationContext;

    public EnumScanClassHandle(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void handle(BeanDefinitionRegistry registry, Set<BeanDefinition> allCandidates) {
        if (!CollectionUtils.isEmpty(allCandidates)) {
            Iterator var3 = allCandidates.iterator();

            while(var3.hasNext()) {
                BeanDefinition candidate = (BeanDefinition)var3.next();

                try {
                    Class clazz = Class.forName(candidate.getBeanClassName());
                    if (clazz.isEnum()) {
                        EnumStaticMap.add(clazz, this.applicationContext);
                    }
                } catch (ClassNotFoundException var6) {
                    log.error("EnumScanClassHandle实例扫描异常 bean=" + candidate.getBeanClassName(), var6);
                }
            }
        }

    }
}
