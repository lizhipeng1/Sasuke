package com.rpc.service;

import com.rpc.annotation.config.ServiceProfileConfig;
import com.rpc.provider.ServiceProviderScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 服务 发布类
 */
@Component
public class PublishServiceOperation implements ApplicationContextAware {
    private static final Logger log= LoggerFactory.getLogger(PublishServiceOperation.class);

    private  ApplicationContext applicationContext;
    private ServiceProviderScanner serviceProviderScanner;

    private ServiceProfileConfig serviceProfileConfig = null;

    @PostConstruct
    public void init(){
        if(serviceProfileConfig!= null && serviceProfileConfig.isStartRpc()){
            log.info(" 执行扫描 Provider 服务发布 ");
            this.serviceProviderScanner = applicationContext.getBean(ServiceProviderScanner.class);
            serviceProviderScanner.scannerBeanInfo();
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        serviceProfileConfig = applicationContext.getBean(ServiceProfileConfig.class);
    }
}
