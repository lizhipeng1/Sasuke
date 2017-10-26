package com.rpc.service;

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

@Component
public class BeanFactoryPostProcessorService implements BeanFactoryPostProcessor , BeanDefinitionRegistryPostProcessor,ApplicationContextAware  {
    public ApplicationContext applicationContext;
    public  ConfigurableListableBeanFactory configurableListableBeanFactory;
    public DefaultListableBeanFactory defaultListableBeanFactory;
    public BeanDefinitionRegistry registry;

    private InvokeServiceOperation invokeServiceOperation;


    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.configurableListableBeanFactory = configurableListableBeanFactory;
//        将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

//         获取bean工厂并转换为DefaultListableBeanFactory
        defaultListableBeanFactory= (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

        // 执行 服务激活
//        invokeServiceOperation = applicationContext.getBean(InvokeServiceOperation.class);
//         invokeServiceOperation.doInvokeScanner();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }
}
