package com.rpc.service;

import com.rpc.invoker.impl.HessianInvokerScannerImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class BeanPostProcessorService implements BeanPostProcessor{

    @Autowired
    private  BeanFactoryPostProcessorService beanFactoryPostProcessorService;
    @Autowired
    HessianInvokerScannerImpl hessianInvokerScanner;

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        hessianInvokerScanner.doRegister( beanName );
        BeanDefinition beanDefinition = beanFactoryPostProcessorService.configurableListableBeanFactory.getBeanDefinition(beanName);

        Object object = beanFactoryPostProcessorService.applicationContext.getBean(beanName);
        return bean;
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        BeanDefinition beanDefinition = beanFactoryPostProcessorService.configurableListableBeanFactory.getBeanDefinition(beanName);
        Object object = beanFactoryPostProcessorService.applicationContext.getBean(beanName);
        return bean;
    }

}
