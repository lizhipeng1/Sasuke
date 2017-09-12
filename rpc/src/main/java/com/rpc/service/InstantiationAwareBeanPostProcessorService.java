//package com.rpc.service;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.stereotype.Component;
//
///**
// * Created by hzlizhipeng on 2017/9/12.
// */
//@Component
//public class InstantiationAwareBeanPostProcessorService extends InstantiationAwareBeanPostProcessorAdapter implements ApplicationContextAware {
//    private  ApplicationContext applicationContext;
//    @Override
//    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        Object object = applicationContext.getBean(beanName);
//        return object;
//    }
//
//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        return super.postProcessAfterInitialization(bean,beanName);
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//        this.applicationContext = applicationContext;
//    }
//}
