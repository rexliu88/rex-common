package com.auxdemo.adp.commons.group.config.holder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;
@Configuration
public class BeanDefinitionRegistryHolder implements BeanFactoryPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(BeanDefinitionRegistryHolder.class);
    private static ConfigurableListableBeanFactory registry;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        registry = configurableListableBeanFactory;
    }

    public static ConfigurableListableBeanFactory getRegistry() {
        return registry;
    }
}

