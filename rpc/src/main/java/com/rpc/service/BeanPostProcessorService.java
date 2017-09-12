package com.rpc.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanPostProcessorService implements BeanPostProcessor , ApplicationContextAware{

    private  BeanFactoryPostProcessorService beanFactoryPostProcessorService;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanFactoryPostProcessorService.configurableListableBeanFactory.getBeanDefinition(beanName);
        Object object = beanFactoryPostProcessorService.applicationContext.getBean(beanName);
        return object;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanFactoryPostProcessorService.configurableListableBeanFactory.getBeanDefinition(beanName);
        Object object = beanFactoryPostProcessorService.applicationContext.getBean(beanName);
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
    }
}
