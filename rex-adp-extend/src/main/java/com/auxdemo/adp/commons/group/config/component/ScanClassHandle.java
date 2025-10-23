package com.auxdemo.adp.commons.group.config.component;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Set;

/**
 * 扫描类处理器接口
 * <p>
 * 该接口定义了处理扫描到的Bean定义的规范，用于在Spring容器初始化过程中
 * 对扫描到的类进行自定义处理。
 * </p>
 */
public interface ScanClassHandle {
    /**
     * 处理扫描到的Bean定义
     * <p>
     * 该方法负责处理扫描过程中发现的Bean定义，可以对这些Bean定义进行注册、
     * 修改或其他自定义操作。
     * </p>
     *
     * @param registry Bean定义注册器，用于注册Bean定义到Spring容器中
     * @param beanDefinitions 扫描到的Bean定义集合，包含需要处理的Bean定义对象
     */
    void handle(BeanDefinitionRegistry registry, Set<BeanDefinition> beanDefinitions);
}
