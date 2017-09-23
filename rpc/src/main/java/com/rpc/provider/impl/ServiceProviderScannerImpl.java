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
import com.rpc.util.Utils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ServiceProviderScannerImpl  implements ServiceProviderScanner , ApplicationContextAware{
    private static final Logger log= LoggerFactory.getLogger(ServiceProviderScannerImpl.class);
    private ApplicationContext applicationContext;

    private BeanFactoryPostProcessorService beanFactoryPostProcessorService;
    private Config config;
    private  List<BeanDefinitionInfo> beanDefinitionInfos;

    public void scannerBeanInfo(){
        this.config = applicationContext.getBean(Config.class);
        this.beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
        doScannerBeanInfo();
        doAllocateBean();
    }

    private void doScannerBeanInfo() {
        List<BeanDefinitionInfo> beanDefinitionInfos = Lists.newArrayList();
        Map<String , Object> beanMap  = applicationContext.getBeansWithAnnotation(ServiceProvider.class);
        if(beanMap!=null && beanMap.size()>0){
            List<String> beanNames = Arrays.asList( beanMap.keySet().toArray( new String[]{} ) );
            for (String tem : beanNames) {
                log.info("spring bean Name  "+ tem);
                BeanDefinition beanDefinition = beanFactoryPostProcessorService.configurableListableBeanFactory.getBeanDefinition(tem);
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

                    beanDefinitionInfos.add(
                            new BeanDefinitionInfo().
                                    setInterfaceClazz(clazz.getInterfaces()[0]).
                                    setServiceClazz(clazz).
                                    setBeanInterfaceName( beanName ).
                                    setRequestUrl(  config.getRpcServerPrefix()+url  ).
                                    setEnvironment( Environment.environment).
                                    setBeanName(tem).
                                    setRpcTypeEnum(serviceProvider.rpcTypeEnum())
                    );
                }
            }
            this.beanDefinitionInfos = beanDefinitionInfos;
        }
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
