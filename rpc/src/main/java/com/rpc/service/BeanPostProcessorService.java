package com.rpc.service;

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

    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if(HessianInvokerScannerImpl.rpcObjectMap.get( beanName )!=null){
            BeanDefinition beanDefinition = beanFactoryPostProcessorService.configurableListableBeanFactory.getBeanDefinition(beanName);
            Object object = beanFactoryPostProcessorService.applicationContext.getBean(beanName);
            return object;
        }else {
            return bean;
        }
    }

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(HessianInvokerScannerImpl.rpcObjectMap.get( beanName )!=null){
            BeanDefinition beanDefinition = beanFactoryPostProcessorService.configurableListableBeanFactory.getBeanDefinition(beanName);
            Object object = beanFactoryPostProcessorService.applicationContext.getBean(beanName);
            return object;
        }else {
            return bean;
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
    }
}
