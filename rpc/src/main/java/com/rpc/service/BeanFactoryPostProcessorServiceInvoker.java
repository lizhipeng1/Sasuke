package com.rpc.service;

import com.rpc.invoker.impl.HessianInvokerScannerImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

public class BeanFactoryPostProcessorServiceInvoker implements BeanFactoryPostProcessor    {

    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        try {
            HessianInvokerScannerImpl hessianInvokerScannerImpl = configurableListableBeanFactory.getBean(HessianInvokerScannerImpl.class);
            hessianInvokerScannerImpl.doInvoke();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
