package com.auxdemo.adp.commons.group.config.holder;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Slf4j
@Configuration
public class BeanDefinitionRegistryHolder implements BeanFactoryPostProcessor {
    private static volatile ConfigurableListableBeanFactory registry;

    @Override
    public void postProcessBeanFactory(@NotNull ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        registry = configurableListableBeanFactory;
    }

    public static ConfigurableListableBeanFactory getRegistry() {
        if (registry == null) {
            log.warn("BeanFactory registry is not initialized yet");
            throw new IllegalStateException("BeanFactory registry is not initialized yet");
        }
        return registry;
    }
}

