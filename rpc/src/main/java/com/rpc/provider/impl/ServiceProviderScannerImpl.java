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
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.lang.annotation.Annotation;
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

    /**
     * 扫描需要发布的 hessian 服务
     */
    private void doScannerBeanInfo() {
        List<BeanDefinitionInfo> beanDefinitionInfos = Lists.newArrayList();
        String[] beanNamesAll = configurableListableBeanFactory.getBeanDefinitionNames();
//        String[] beanNames = configurableListableBeanFactory.getBeanNamesForAnnotation(ServiceProvider.class);    //spring 4.x.x
        String[] beanNamesWithAnnotation =   getBeanNamesForAnnotation(ServiceProvider.class , beanNamesAll);
        if(beanNamesWithAnnotation!=null && beanNamesWithAnnotation.length>0){
            for (String tempBeanName : beanNamesWithAnnotation) {
                log.info("spring bean Name  "+ tempBeanName);
                BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(tempBeanName);
                if(beanDefinition ==null){ continue; }
                String className = beanDefinition.getBeanClassName();
                if(StringUtils.isBlank(className)){continue;}
                Class clazz;
                try {
                    clazz = Class.forName(className);
                } catch (ClassNotFoundException e) {
                    log.info(e.toString());
                    continue;
                }
                if(clazz != null) {
                    Class[] parentClass = clazz.getInterfaces();
                    if (parentClass != null || parentClass.length > 0) {
                        String parentName = parentClass[0].getSimpleName();
                        String beanName = parentName.substring(0, 1).toLowerCase() + parentName.substring(1);
                        String url = "/" + beanName;
                        ServiceProvider serviceProvider = (ServiceProvider) clazz.getAnnotation(ServiceProvider.class);
                        if (serviceProvider != null) {
                            BeanDefinitionInfo beanDefinitionInfo = new BeanDefinitionInfo();
                            beanDefinitionInfo.setInterfaceClazz(clazz.getInterfaces()[0]);
                            beanDefinitionInfo.setServiceClazz(clazz);
                            beanDefinitionInfo.setBeanInterfaceName(beanName);
                            beanDefinitionInfo.setRequestUrl(config.getRpcServerPrefix() + url);
                            beanDefinitionInfo.setEnvironment(Environment.environment);
                            beanDefinitionInfo.setBeanName(tempBeanName);
                            beanDefinitionInfo.setRpcTypeEnum(serviceProvider.rpcTypeEnum());
                            beanDefinitionInfos.add(beanDefinitionInfo);
                        }
                    }
                }
            }
            this.beanDefinitionInfos = beanDefinitionInfos;
        }
    }

    /**
     * 根据服务类型具体分配 调用哪种 分配方法
     */
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

    public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType , String[] beanDefinitionNames) {
        List<String> results = new ArrayList<String>();
        for (String beanName : beanDefinitionNames) {
            BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanName);
            if (!beanDefinition.isAbstract() && configurableListableBeanFactory.findAnnotationOnBean(beanName, annotationType) != null) {
                results.add(beanName);
            }
        }
        return results.toArray(new String[results.size()]);
    }


    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
