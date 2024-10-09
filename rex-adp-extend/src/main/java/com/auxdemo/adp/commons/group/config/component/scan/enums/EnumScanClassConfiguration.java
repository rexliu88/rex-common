package com.auxdemo.adp.commons.group.config.component.scan.enums;

import com.auxdemo.adp.commons.group.config.component.AuxClassPathBeanDefinitionScanner;
import com.auxdemo.adp.commons.group.config.component.AuxTypeFilterInterface;
import com.auxdemo.adp.commons.group.config.holder.BeanDefinitionRegistryHolder;
import com.auxgroup.adp.commons.group.config.enums.AuxEnum;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.type.filter.TypeFilter;

@Configuration
@ConditionalOnProperty(
        value = {"aux.enums.enabled"},
        havingValue = "true",
        matchIfMissing = true
)
public class EnumScanClassConfiguration implements InitializingBean, ApplicationContextAware, Ordered {
    private ApplicationContext applicationContext;
    private static String[] scanPackage = new String[]{"com.auxgroup"};
    @Override
    public void afterPropertiesSet() throws Exception {
        AuxClassPathBeanDefinitionScanner scanner = new AuxClassPathBeanDefinitionScanner(
                (BeanDefinitionRegistry) BeanDefinitionRegistryHolder.getRegistry(),
                new TypeFilter[]{new AuxTypeFilterInterface(AuxEnum.class)}
        );
        scanner.setResourceLoader(this.applicationContext);
        scanner.setScanClassHandle(new EnumScanClassHandle(this.applicationContext));
        scanner.scan(scanPackage);
    }
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public int getOrder() {
        return 300000;
    }
}
