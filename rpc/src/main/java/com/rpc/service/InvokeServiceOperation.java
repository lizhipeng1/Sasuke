package com.rpc.service;

import com.rpc.annotation.config.ServiceProfileConfig;
import com.rpc.invoker.HessianInvokerOperator;
import com.rpc.invoker.ServiceInvokeScanner;
import com.rpc.invoker.ServiceInvokerOperator;
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

    private BeanFactoryPostProcessorService beanFactoryPostProcessorService;

    private ServiceInvokerOperator hessianInvokerOperator;



    public void doInvokeRegisterToSpring(){
        if(serviceProfileConfig!= null && serviceProfileConfig.isStartRpc()) {
            log.info(" 执行扫描 Invoke 服务 注入spring 容器");
            this.serviceInvokeScanner = beanFactoryPostProcessorService.getDefaultListableBeanFactory().getBean( ServiceInvokeScanner.class );
            this.serviceInvokeScanner.scannerAllocateBeanInfo();
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.serviceProfileConfig = applicationContext.getBean(ServiceProfileConfig.class);
        this.beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
        this.hessianInvokerOperator = applicationContext.getBean(HessianInvokerOperator.class);

    }

    public void invokeService(Object bean, String beanName) {
        hessianInvokerOperator.invokeService(bean , beanName);
    }
}
