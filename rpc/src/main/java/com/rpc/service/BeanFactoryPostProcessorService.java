package com.rpc.service;

import com.rpc.annotation.config.ServiceProfileConfig;
import com.rpc.provider.ServiceProviderScanner;
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

    private ServiceProfileConfig serviceProfileConfig = null;

    private ServiceProviderScanner serviceProviderScanner;


    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.configurableListableBeanFactory = configurableListableBeanFactory;

        ConfigurableApplicationContext configurableApplicationContext = (ConfigurableApplicationContext) applicationContext;

        defaultListableBeanFactory= (DefaultListableBeanFactory) configurableApplicationContext.getBeanFactory();

        doRegisterToSpring();

        doPublishAndRegisterHessianToSpring();

    }

    /**
     * 执行发 rpc 服务 上传服务的 url 调用信息
     */
    private void doPublishAndRegisterHessianToSpring() {
        serviceProviderScanner = applicationContext.getBean(ServiceProviderScanner.class);
        serviceProviderScanner.scannerBeanInfo();
    }

    /**
     * 执行 注册 hessian 服务到 spring bean 的容器中
     */
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
