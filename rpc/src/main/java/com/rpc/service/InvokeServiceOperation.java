package com.rpc.service;

import com.rpc.annotation.config.ServiceProfileConfig;
import com.rpc.invoker.ServiceInvokeScanner;
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
public class InvokeServiceOperation implements ApplicationContextAware {
    private static final Logger log= LoggerFactory.getLogger(InvokeServiceOperation.class);

    private ServiceInvokeScanner serviceInvokeScanner;
    private  ApplicationContext applicationContext;

    private ServiceProfileConfig serviceProfileConfig = null;

    @PostConstruct
    public void init(){
        doInvokeScanner();
    }


    public void doInvokeScanner(){
        if(serviceProfileConfig!= null && serviceProfileConfig.isStartRpc()) {
            log.info(" 执行扫描 Invoke 服务发布 ");
            this.serviceInvokeScanner = applicationContext.getBean(ServiceInvokeScanner.class);
            this.serviceInvokeScanner.scannerAllocateBeanInfo();
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        serviceProfileConfig = applicationContext.getBean(ServiceProfileConfig.class);
    }
}
