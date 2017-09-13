package com.rpc.invoker.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rpc.Environment;
import com.rpc.annotation.Invoker.ServiceInvokerAutowired;
import com.rpc.annotation.Invoker.ServiceInvokerResource;
import com.rpc.annotation.spring.RpcServiceInvoke;
import com.rpc.bean.model.BeanDefinitionInfo;
import com.rpc.config.Config;
import com.rpc.invoker.HessianInvokerScanner;
import com.rpc.service.BeanFactoryPostProcessorService;
import com.rpc.service.BeanPostProcessorService;
import com.rpc.util.HessianUtil;
import com.rpc.util.ReflectionUtils;
import com.rpc.util.ZKUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
public class HessianInvokerScannerImpl implements HessianInvokerScanner , ApplicationContextAware{
    private static final Logger log= LoggerFactory.getLogger(HessianInvokerScannerImpl.class);

    private BeanFactoryPostProcessorService beanFactoryPostProcessorService;

    private ApplicationContext applicationContext;

    private List<BeanDefinitionInfo> beanDefinitionInfoList=null;

    private Map<String , BeanDefinitionInfo> interfaceNameMap;

    public static Map<String , List<String>> rpcObjectMap = new HashMap<String, List<String>>();

    private Config config;

    public void doInvoke() {
        this.beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
        this.config = applicationContext.getBean(Config.class);
        try {
            getBeanDefinition();
            invokeService();
            registerSpring();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    public void doRegister(String name){
//        if(!CollectionUtils.isEmpty(interfaceNameMap)){
//            registerSpring(name);
//        }
//    }


    public List<BeanDefinitionInfo> getBeanDefinition() throws Exception {
        beanDefinitionInfoList = Lists.newArrayList();
        interfaceNameMap = Maps.newHashMap();
        // 先获取 所有包含InvokeService 注解的属性
        String[] beanDefinitionNames = beanFactoryPostProcessorService.configurableListableBeanFactory.getBeanDefinitionNames();
//        Map<String , Object> beanMaps = beanFactoryPostProcessorService.configurableListableBeanFactory.getBeansWithAnnotation(RpcServiceInvoke.class);
        if(beanDefinitionNames != null && beanDefinitionNames.length>0) {
            for (int i= 0 ; i<beanDefinitionNames.length ; i++) {
                String beanName = beanDefinitionNames[i];
                if(!applicationContext.containsBean( beanName )){
                    continue;
                }
                Object proxy = applicationContext.getBean( beanName );
                Object object = ReflectionUtils.getTarget( proxy );
                Field[] fields =object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    log.info(field.getName());
                    ServiceInvokerResource serviceInvokerResource = field.getAnnotation(ServiceInvokerResource.class);
                    ServiceInvokerAutowired serviceInvokerAutowired = field.getAnnotation(ServiceInvokerAutowired.class);
                    BeanDefinitionInfo beanDefinitionInfo = null;
                    if (serviceInvokerResource != null) {
                        beanDefinitionInfo = doDealInvokeResource(field, serviceInvokerResource);
                    }
                    if (serviceInvokerAutowired != null) {
                        beanDefinitionInfo = doDealInvokeAutowired(field, serviceInvokerAutowired);
                    }
                    if (beanDefinitionInfo != null) {
                        beanDefinitionInfoList.add(beanDefinitionInfo);
                        interfaceNameMap.put(field.getType().getSimpleName(), beanDefinitionInfo);

                        addToRpcObjectMap(beanName , field);

                    }
                }
            }
        }
        return beanDefinitionInfoList;
    }

    private void addToRpcObjectMap(String beanName, Field field) {
        List<String> propNames = null;
        if(rpcObjectMap.get( beanName )!=null && rpcObjectMap.get( beanName ).size()>0){
            propNames= rpcObjectMap.get(beanName);
        }else {
            propNames = new ArrayList<String>();
        }
        propNames.add( field.getType().getSimpleName());
        rpcObjectMap.put(beanName, propNames);
    }

    // 处理 Autowaire 注入
    private BeanDefinitionInfo doDealInvokeAutowired(Field field, ServiceInvokerAutowired serviceInvokerAutowired) {
        Class propClazz = field.getType();
        if (interfaceNameMap.containsKey(propClazz.getName())) {
            BeanDefinitionInfo tempBeanDefinitionInfo = interfaceNameMap.get(propClazz.getName());
            if (tempBeanDefinitionInfo.getEnvironment().equals(Environment.environment)) {
                return null;
            }
        }
        BeanDefinitionInfo beanDefinitionInfo = new BeanDefinitionInfo();
        beanDefinitionInfo.setBeanName(field.getName());
        beanDefinitionInfo.setInterfaceClazz(propClazz);
        beanDefinitionInfo.setEnvironment(  Environment.environment );
        return beanDefinitionInfo;
    }
    // 处理 resource 注入
    private BeanDefinitionInfo doDealInvokeResource(Field field , ServiceInvokerResource serviceInvokerResource) {
        Class propClazz = field.getType();
        if (interfaceNameMap.containsKey(propClazz.getName())) {
            BeanDefinitionInfo tempBeanDefinitionInfo = interfaceNameMap.get(propClazz.getName());
            if (tempBeanDefinitionInfo.getEnvironment().equals(serviceInvokerResource.value())) {
                return null;
            }
        }
        BeanDefinitionInfo beanDefinitionInfo = new BeanDefinitionInfo();
        beanDefinitionInfo.setBeanName(field.getName());
        beanDefinitionInfo.setInterfaceClazz(propClazz);
        beanDefinitionInfo.setEnvironment(StringUtils.isEmpty(serviceInvokerResource.value()) ? Environment.environment : serviceInvokerResource.value());
        return beanDefinitionInfo;
    }

    public void invokeService() throws Exception {
        if(CollectionUtils.isEmpty(beanDefinitionInfoList)){
            log.info(" empty List ");
            return;
        }
        for(BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoList){

            String nodeName = "/"+config.getProjectName()+"-"+Environment.environment;
            nodeName+="/"+beanDefinitionInfo.getInterfaceClazz().getSimpleName();

            String jsonZKStr = ZKUtil.getNodeData(nodeName);
            if(jsonZKStr==null || jsonZKStr.length()==0){ continue;}
            String  beanDefinitionStr  =   JSONObject.parse(jsonZKStr).toString();

            if(StringUtils.isEmpty(beanDefinitionStr)){
                log.info("未找到环境："+beanDefinitionInfo.getEnvironment()+" 下的服务 ："+beanDefinitionInfo.getInterfaceClazz().getName());
                continue;
            }
            BeanDefinitionInfo beanDefinitionInfoRpc =  JSONObject.parseObject( beanDefinitionStr, BeanDefinitionInfo.class);
            if(beanDefinitionInfoRpc == null){
                log.info("未找到环境："+beanDefinitionInfo.getEnvironment()+" 下的服务 ："+beanDefinitionInfo.getInterfaceClazz().getName());
                continue;
            }
            BeanUtils.copyProperties( beanDefinitionInfoRpc , beanDefinitionInfo );

            /*
                判断 在容器中是不是已经存在
                这里会判断当前项目中是不是已经存在对应的 远程调用的类（会出现当前项目 即暴露 又引用 远程服务的情况）
                本来这里想当前项目就直接引用档期啊spring 内的服务就可以了不用远程调用
                但是这里 如果 a 实现了 b  当前项目的环境是 dev 我想引入一个 qa 环境的 a  这样的话 这里的判断就会被过滤掉了
             */
            if( beanDefinitionInfo.getServiceClazz()!=null &&  beanFactoryPostProcessorService.applicationContext.getBean( beanDefinitionInfo.getServiceClazz() ) != null
                    && Environment.environment.equals(beanDefinitionInfo.getEnvironment()) ){
                log.info("当前容器中已经存在 环境为："+beanDefinitionInfo.getEnvironment()+"的 bean实例：" + beanDefinitionInfo.getServiceClazz().getName()  + " 不在注册 rpc 服务bean,使用本容器中的bean实例");
                continue;
            }

            try {
                beanDefinitionInfo.setServiceObject( HessianUtil.factory.create(beanDefinitionInfo.getInterfaceClazz() , beanDefinitionInfo.getRequestUrl()) );
            } catch (MalformedURLException e) {
                log.info("获取 bean "+beanDefinitionInfo.getInterfaceClazz().getName() + " 出现异常 "+e.toString());
                continue;
            }
        }

    }

    public void registerSpring() {
        if(CollectionUtils.isEmpty(beanDefinitionInfoList)){
            log.info(" empty List ");
            return ;
        }
        for (Iterator iterator = rpcObjectMap.keySet().iterator() ; iterator.hasNext();){
            String rpcBeanClassName = (String) iterator.next();
            List<String> propBeanNames = rpcObjectMap.get(rpcBeanClassName);
            if(propBeanNames!=null && propBeanNames.size()>0){
                for(String propBeanName : propBeanNames){
                    BeanDefinitionInfo beanDefinitionInfo = interfaceNameMap.get(propBeanName);

                    String propNameFinal = propBeanName.substring(0,1).toLowerCase()+propBeanName.substring(1,propBeanName.length());


                    DefaultListableBeanFactory defaultListableBeanFactory = beanFactoryPostProcessorService.defaultListableBeanFactory;
                    ScannedGenericBeanDefinition rpcBeanDefinition = (ScannedGenericBeanDefinition) defaultListableBeanFactory.getBeanDefinition(rpcBeanClassName);

                    MutablePropertyValues pv =  rpcBeanDefinition.getPropertyValues();
//                    if(pv.contains(propBeanName)){
                        pv.addPropertyValue(propNameFinal , beanDefinitionInfo.getServiceObject());
//                    }
                    defaultListableBeanFactory.registerBeanDefinition(rpcBeanClassName, rpcBeanDefinition);
//                    beanFactoryPostProcessorService.registry.registerBeanDefinition(rpcBeanClassName, rpcBeanDefinition);
//                    beanFactoryPostProcessorService.defaultListableBeanFactory.registerBeanDefinition( rpcBeanClassName , rpcBeanDefinition);
//                    Object rpcValueObject = this.applicationContext.getBean(rpcBeanName);
//
//                    ReflectionUtils.setFieldValue( rpcValueObject ,propNameFinal , beanDefinitionInfo.getServiceObject());
//
//                    beanFactoryPostProcessorService.defaultListableBeanFactory.removeBeanDefinition( rpcBeanName );
//
//                    beanFactoryPostProcessorService.configurableListableBeanFactory.registerSingleton(rpcBeanName,rpcValueObject);

//                    beanFactoryPostProcessorService.configurableListableBeanFactory.registerSingleton(propBeanName, beanDefinitionInfo.getServiceObject());
//                    log.info("注册bean" + beanDefinitionInfo.getInterfaceClazz().getName() + " 到 rpcBeanName 中的属性中");
                }
            }
        }
    }

    public List<BeanDefinitionInfo> getBeanDefinitionInfoList() {
        return beanDefinitionInfoList;
    }

    public Map<String, List<String>> getRpcObjectMap() {
        return rpcObjectMap;
    }

    public Map<String, BeanDefinitionInfo> getInterfaceNameMap() {
        return interfaceNameMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

