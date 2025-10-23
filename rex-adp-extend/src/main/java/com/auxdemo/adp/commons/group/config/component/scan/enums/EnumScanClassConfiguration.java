package com.auxdemo.adp.commons.group.config.component.scan.enums;

import com.auxdemo.adp.commons.group.config.component.AuxClassPathBeanDefinitionScanner;
import com.auxdemo.adp.commons.group.config.component.AuxTypeFilterInterface;
import com.auxdemo.adp.commons.group.config.holder.BeanDefinitionRegistryHolder;
import com.auxgroup.adp.commons.group.config.enums.AuxEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.type.filter.TypeFilter;

import java.util.Arrays;

@Slf4j
@Configuration
@ConditionalOnProperty(
        value = {"aux.enums.enabled"},
        havingValue = "true",
        matchIfMissing = true
)
public class EnumScanClassConfiguration implements InitializingBean, ApplicationContextAware, Ordered {
    private ApplicationContext applicationContext;
    // 可选优化项：从配置文件获取扫描路径
    // @Value("${aux.enum.scan.packages:com.auxgroup}")
    private static final String[] SCAN_PACKAGES = new String[]{"com.auxgroup"};
    @Override
    public void afterPropertiesSet() throws Exception {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext has not been injected.");
        }

        ConfigurableListableBeanFactory registryObj = BeanDefinitionRegistryHolder.getRegistry();
        if (!(registryObj instanceof BeanDefinitionRegistry)) {
            throw new IllegalStateException("Registry is not an instance of BeanDefinitionRegistry");
        }

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) registryObj;

        AuxClassPathBeanDefinitionScanner scanner = new AuxClassPathBeanDefinitionScanner(
                registry,
                new TypeFilter[]{new AuxTypeFilterInterface(AuxEnum.class)}
        );

        scanner.setResourceLoader(this.applicationContext);
        scanner.setScanClassHandle(new EnumScanClassHandle(this.applicationContext));

        log.info("Starting enum scanning in packages: {}", Arrays.toString(SCAN_PACKAGES));
        int count = scanner.scan(SCAN_PACKAGES);
        log.info("Enum scanning completed. Found {} candidates.", count);
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
