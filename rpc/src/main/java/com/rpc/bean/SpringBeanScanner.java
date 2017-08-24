package com.rpc.bean;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 从spring 中获取 redis  等等的 操作类 转为静态类
 */
@Component
public class SpringBeanScanner implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @PostConstruct
    public void init(){

    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
