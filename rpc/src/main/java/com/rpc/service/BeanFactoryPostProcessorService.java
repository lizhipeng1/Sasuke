package com.rpc.service;

import com.alibaba.fastjson.JSONObject;
import com.rpc.annotation.config.ServiceProfileConfig;
import com.rpc.provider.ServiceProviderScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
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

    private ServiceProfileConfig serviceProfileConfig = null;

    private ServiceProviderScanner serviceProviderScanner;


    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.configurableListableBeanFactory = configurableListableBeanFactory;

//        将applicationContext转换为ConfigurableApplicationContext
        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

//         获取bean工厂并转换为DefaultListableBeanFactory
        defaultListableBeanFactory= (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

        doRegisterToSpring();

        //hessian 服务到spring 中
        doRegisterHessianToSpring();

    }

    private void doRegisterHessianToSpring() {
        serviceProviderScanner = applicationContext.getBean(ServiceProviderScanner.class);
        serviceProviderScanner.scannerBeanInfo();
    }

    private void doRegisterToSpring() {
        // 判断是否需要执行rpc 服务
        if(serviceProfileConfig!= null && serviceProfileConfig.isStartRpc()) {
            invokeServiceOperation = defaultListableBeanFactory.getBean(InvokeServiceOperation.class);
            invokeServiceOperation.doInvokeRegisterToSpring();
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        serviceProfileConfig = applicationContext.getBean(ServiceProfileConfig.class);
    }

    public DefaultListableBeanFactory getDefaultListableBeanFactory() {
        return defaultListableBeanFactory;
    }

    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        this.registry = registry;
    }
}
