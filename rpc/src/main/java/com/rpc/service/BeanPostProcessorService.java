package com.rpc.service;

import com.rpc.invoker.HessianInvokerScanner;
import com.rpc.invoker.impl.HessianInvokerScannerImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanPostProcessorService implements BeanPostProcessor , ApplicationContextAware{

    private  BeanFactoryPostProcessorService beanFactoryPostProcessorService;
    private ApplicationContext applicationContext;
    private HessianInvokerScannerImpl hessianInvokerScanner = null;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        hessianInvokerScanner.registerSpring(  beanName );
        BeanDefinition beanDefinition = beanFactoryPostProcessorService.configurableListableBeanFactory.getBeanDefinition(beanName);
        Object object = beanFactoryPostProcessorService.applicationContext.getBean(beanName);
        return object;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
        hessianInvokerScanner =  applicationContext.getBean(HessianInvokerScannerImpl.class);
    }
}
