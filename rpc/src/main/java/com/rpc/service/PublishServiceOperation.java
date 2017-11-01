package com.rpc.service;

import com.rpc.annotation.config.ServiceProfileConfig;
import com.rpc.enums.RpcTypeEnum;
import com.rpc.exec.ThreadExecutor;
import com.rpc.invoker.ServiceInvokerOperator;
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
public class PublishServiceOperation implements ApplicationContextAware {
    private static final Logger log= LoggerFactory.getLogger(PublishServiceOperation.class);

    private  ApplicationContext applicationContext;

    private ServiceProfileConfig serviceProfileConfig = null;

    private ThreadExecutor threadExecutor;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        serviceProfileConfig = applicationContext.getBean(ServiceProfileConfig.class);
        threadExecutor = applicationContext.getBean(ThreadExecutor.class);
    }
}
