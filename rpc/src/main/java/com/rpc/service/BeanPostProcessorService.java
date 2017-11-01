package com.rpc.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanPostProcessorService implements BeanPostProcessor , ApplicationContextAware{

    private  BeanFactoryPostProcessorService beanFactoryPostProcessorService;
    private ApplicationContext applicationContext;

    private InvokeServiceOperation invokeServiceOperation;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        doFilterInvokeSerice( bean  , beanName);
        return bean;
    }

    private void doFilterInvokeSerice(Object bean, String beanName) {
        invokeServiceOperation.invokeService( bean, beanName );
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
        this.applicationContext = applicationContext;
        this.invokeServiceOperation = applicationContext.getBean(InvokeServiceOperation.class);
    }
}
