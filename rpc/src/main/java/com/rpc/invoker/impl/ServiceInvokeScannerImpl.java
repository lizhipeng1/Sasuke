package com.rpc.invoker.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rpc.Environment;
import com.rpc.annotation.Invoker.ServiceInvokerAutowired;
import com.rpc.annotation.Invoker.ServiceInvokerResource;
import com.rpc.bean.model.BeanDefinitionInfo;
import com.rpc.enums.RpcTypeEnum;
import com.rpc.invoker.ServiceInvokeScanner;
import com.rpc.invoker.ServiceInvokerOperator;
import com.rpc.service.BeanFactoryPostProcessorService;
import com.rpc.util.ReflectionUtils;
import com.rpc.util.Utils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ServiceInvokeScannerImpl implements ServiceInvokeScanner, ApplicationContextAware{
    private static final Logger log= LoggerFactory.getLogger(ServiceInvokeScannerImpl.class);
    private ApplicationContext applicationContext;

    private BeanFactoryPostProcessorService beanFactoryPostProcessorService;
    private DefaultListableBeanFactory defaultListableBeanFactory;
    private  List<BeanDefinitionInfo> beanDefinitionInfos;
    private Map<String , List<String>> springBeanProp = null;

    public void scannerAllocateBeanInfo(){
        this.beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
        this.defaultListableBeanFactory = beanFactoryPostProcessorService.getDefaultListableBeanFactory();
        doScannerBeanInfo();
        doAllocateBean();
    }
    /**
     * 获取 bean 的注册信息 从当前项目的中的服务激活注解中获取
     * 需要注入的 属性的配置信息
     * @return
     * @throws Exception
     */
    private void doScannerBeanInfo() {
        beanDefinitionInfos = Lists.newArrayList();
        springBeanProp = Maps.newHashMap();
        // 先获取 所有包含InvokeService 注解的属性
        String[] beanDefinitionNames = defaultListableBeanFactory.getBeanDefinitionNames();
        System.out.println( JSONObject.toJSONString( beanDefinitionNames ));
        if(beanDefinitionNames != null && beanDefinitionNames.length>0) {
            for (int i= 0 ; i<beanDefinitionNames.length ; i++) {
                String beanName = beanDefinitionNames[i];

                BeanDefinition  beanDefinition =defaultListableBeanFactory.getBeanDefinition(beanName);
                String beanClassName = beanDefinition.getBeanClassName();
                if(!StringUtils.isEmpty(beanClassName)) {
                    Class clazz = null;
                    try {
                        clazz = Class.forName(beanClassName);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    while (clazz != null && !"java.lang.Object".equals(clazz.getName())) {
                        doGetFieldAnnotation(clazz, beanName);
                        clazz = clazz.getSuperclass();
                    }
                }

            }
        }
    }

    private void doGetFieldAnnotation(Class clazz , String springBeanName) {
        if(clazz != null) {
            Field[] fields =clazz.getDeclaredFields();
            for (Field field : fields) {
                ServiceInvokerResource serviceInvokerResource = field.getAnnotation(ServiceInvokerResource.class);
                ServiceInvokerAutowired serviceInvokerAutowired = field.getAnnotation(ServiceInvokerAutowired.class);
                BeanDefinitionInfo beanDefinitionInfo = null;
                if (serviceInvokerResource != null) {
                    beanDefinitionInfo = doDealInvokeResource(springBeanName , field, serviceInvokerResource);
                }
                if (serviceInvokerAutowired != null) {
                    beanDefinitionInfo = doDealInvokeAutowired(springBeanName , field, serviceInvokerAutowired);
                }
                if (beanDefinitionInfo != null) {
                    beanDefinitionInfo.setSpringBeanName( springBeanName );
                    beanDefinitionInfos.add(beanDefinitionInfo);
                }
            }
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

                ServiceInvokerOperator serviceInvokerOperator = (ServiceInvokerOperator) this.applicationContext.getBean( rpcTypeEnum.getInvokeClazz() );
                serviceInvokerOperator.doInvoke( beanDefinitionInfosTemp );
            }
        }
    }

    /**
     *处理 Autowaire 注入
     * 装载到map 中
     */
    private BeanDefinitionInfo doDealInvokeAutowired(String beanName, Field field, ServiceInvokerAutowired serviceInvokerAutowired) {
        Class propClazz = field.getType();
        if(!doFillSpringPropsMap( beanName , field )){
            return null;
        }
        BeanDefinitionInfo beanDefinitionInfo = new BeanDefinitionInfo();
        beanDefinitionInfo.setBeanName(field.getName());
        beanDefinitionInfo.setInterfaceClazz(propClazz);
        beanDefinitionInfo.setFiledName(field.getName());
        beanDefinitionInfo.setEnvironment(  Environment.environment );
        beanDefinitionInfo.setRpcTypeEnum(serviceInvokerAutowired.rpcTypeEnum());
        return beanDefinitionInfo;
    }
    /**
     * 处理 resource 注入
     * 有做 环境版本相关信息的过滤
     * 装载到map 中
     */
    private BeanDefinitionInfo doDealInvokeResource(String beanName, Field field, ServiceInvokerResource serviceInvokerResource) {
        Class propClazz = field.getType();
        if(!doFillSpringPropsMap( beanName , field )){
            return null;
        }
        BeanDefinitionInfo beanDefinitionInfo = new BeanDefinitionInfo();
        beanDefinitionInfo.setBeanName(field.getName());
        beanDefinitionInfo.setInterfaceClazz(propClazz);
        beanDefinitionInfo.setFiledName(field.getName());
        beanDefinitionInfo.setEnvironment(StringUtils.isEmpty(serviceInvokerResource.value()) ? Environment.environment : serviceInvokerResource.value());
        beanDefinitionInfo.setRpcTypeEnum(serviceInvokerResource.rpcTypeEnum());
        return beanDefinitionInfo;
    }

    private boolean doFillSpringPropsMap(String beanName, Field field) {
        List<String> springBeanProps = null;
        if (springBeanProp.containsKey(beanName)) {
            springBeanProps = springBeanProp.get( beanName );
            if(springBeanProps.contains( field.getName() )){
                return false;
            }else {
                springBeanProps.add( field.getName() );
            }
        }else {
            springBeanProps = new ArrayList<String>();
            springBeanProps.add( field.getName() );
        }
        springBeanProp.put(beanName , springBeanProps);
        return true;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
