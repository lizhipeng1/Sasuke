package com.rpc.provider.impl;

import com.google.common.collect.Lists;
import com.rpc.Environment;
import com.rpc.annotation.provider.ServiceProvider;
import com.rpc.bean.model.BeanDefinitionInfo;
import com.rpc.config.Config;
import com.rpc.enums.RpcTypeEnum;
import com.rpc.provider.ServiceProviderOperator;
import com.rpc.provider.ServiceProviderScanner;
import com.rpc.service.BeanFactoryPostProcessorService;
import com.rpc.parent.RpcService;
import com.rpc.util.ReflectionUtils;
import com.rpc.util.Utils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ServiceProviderScannerImpl  implements ServiceProviderScanner , ApplicationContextAware{
    private static final Logger log= LoggerFactory.getLogger(ServiceProviderScannerImpl.class);
    private ApplicationContext applicationContext;

    private BeanFactoryPostProcessorService beanFactoryPostProcessorService;
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    private Config config;
    private  List<BeanDefinitionInfo> beanDefinitionInfos;

    public void scannerBeanInfo(){
        this.config = applicationContext.getBean(Config.class);
        this.beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
        this.configurableListableBeanFactory = beanFactoryPostProcessorService.configurableListableBeanFactory;
        doScannerBeanInfo();
        doAllocateBean();
    }

    private void doScannerBeanInfo() {
        List<BeanDefinitionInfo> beanDefinitionInfos = Lists.newArrayList();
        String[] beanNames = configurableListableBeanFactory.getBeanNamesForAnnotation(ServiceProvider.class);
        if(beanNames!=null && beanNames.length>0){
            for (String tempBeanName : beanNames) {
                log.info("spring bean Name  "+ tempBeanName);
                BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(tempBeanName);
                String className = beanDefinition.getBeanClassName();
                Class clazz = null;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    log.info(e.toString());
                }
                if(clazz != null){
                    String parentName =clazz.getInterfaces()[0].getSimpleName();
                    String beanName = parentName.substring(0,1).toLowerCase()+parentName.substring(1);
                    String url = "/" +beanName;
                    ServiceProvider serviceProvider = (ServiceProvider) clazz.getAnnotation(ServiceProvider.class);
                    BeanDefinitionInfo beanDefinitionInfo =  new BeanDefinitionInfo();
                    beanDefinitionInfo.setInterfaceClazz(clazz.getInterfaces()[0]);
                    beanDefinitionInfo.setServiceClazz(clazz);
                    beanDefinitionInfo.setBeanInterfaceName( beanName );
                    beanDefinitionInfo.setRequestUrl(  config.getRpcServerPrefix()+url  );
                    beanDefinitionInfo.setEnvironment( Environment.environment);
                    beanDefinitionInfo.setBeanName( tempBeanName );
                    beanDefinitionInfo.setRpcTypeEnum(serviceProvider.rpcTypeEnum());
                    beanDefinitionInfos.add( beanDefinitionInfo );
                }
            }
            this.beanDefinitionInfos = beanDefinitionInfos;
        }
    }

    private Map<String, Object> doGetRpcBeanMap() {
        Map<String, Object> maps = new HashMap<String, Object>();
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for(String beanDname : beanDefinitionNames){
            Object object = applicationContext.getBean(beanDname);
            Object realObject = ReflectionUtils.getTarget(object);
            if(realObject!= null &&  realObject instanceof RpcService){
                maps.put(beanDname , object);
            }
        }
        return null;
    }

    private void doAllocateBean() {
        if(CollectionUtils.isNotEmpty( this.beanDefinitionInfos )) {
            Map<RpcTypeEnum, List<BeanDefinitionInfo>> rpcTypeEnumListMap = Utils.list2MapList(beanDefinitionInfos, new Utils.KeyGenerator<BeanDefinitionInfo>() {
                @Override
                public RpcTypeEnum generate(BeanDefinitionInfo beanDefinitionInfo) {
                    return beanDefinitionInfo.getRpcTypeEnum();
                }
            });
            for(Iterator iterator = rpcTypeEnumListMap.keySet().iterator(); iterator.hasNext();){
                RpcTypeEnum rpcTypeEnum = (RpcTypeEnum) iterator.next();
                List<BeanDefinitionInfo> beanDefinitionInfosTemp = rpcTypeEnumListMap.get( rpcTypeEnum );
                ServiceProviderOperator serviceProviderOperator = (ServiceProviderOperator) this.applicationContext.getBean( rpcTypeEnum.getProviderClazz() );
                serviceProviderOperator.doProvider( beanDefinitionInfosTemp );
            }
        }
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
