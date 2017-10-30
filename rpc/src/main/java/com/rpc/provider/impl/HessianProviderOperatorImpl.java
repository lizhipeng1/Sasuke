package com.rpc.provider.impl;

import com.rpc.bean.model.BeanDefinitionInfo;
import com.rpc.config.Config;
import com.rpc.provider.HessianProviderOperator;
import com.rpc.service.BeanFactoryPostProcessorService;
import com.rpc.util.ServiceZKNodeNameUtil;
import com.rpc.util.ZKUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
public class HessianProviderOperatorImpl extends HessianProviderOperator implements ApplicationContextAware{
    private static final Logger log= LoggerFactory.getLogger(HessianProviderOperatorImpl.class);
    private ApplicationContext applicationContext;

    private  List<BeanDefinitionInfo> beanDefinitionInfoList;
    private BeanFactoryPostProcessorService beanFactoryPostProcessorService;
    private Config config;
    private ZKUtil zkUtil;

    public void doProvider(List<BeanDefinitionInfo> beanDefinitionInfoList){
        this.beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
        this.config = applicationContext.getBean(Config.class);
        this.beanDefinitionInfoList = beanDefinitionInfoList;
        try {
            if(!CollectionUtils.isEmpty(this.beanDefinitionInfoList)) {
                publishRemote();
                registerManager();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void publishRemote() throws  Exception {
        for (BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoList) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(HessianServiceExporter.class);
            builder.addPropertyReference("service", beanDefinitionInfo.getBeanName());
            builder.addPropertyValue("serviceInterface", beanDefinitionInfo.getInterfaceClazz());
            ((BeanDefinitionRegistry) beanFactoryPostProcessorService.configurableListableBeanFactory).registerBeanDefinition("/"+beanDefinitionInfo.getBeanInterfaceName(),
                    builder.getBeanDefinition());
        }
    }

    public void registerManager()  throws  Exception{
        for(BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoList){
            String nodeName = ServiceZKNodeNameUtil.getServiceZKNodeName( beanDefinitionInfo.getEnvironment()  ,
                    beanDefinitionInfo.getInterfaceClazz().getName() );
            if(zkUtil.exitNode(nodeName)){
                zkUtil.addNodeData( nodeName , beanDefinitionInfo);
            }else {
                zkUtil.createNodeWithData( nodeName , beanDefinitionInfo);
            }
        }
    }



    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        this.zkUtil = applicationContext.getBean(ZKUtil.class);
    }
}
