package com.rpc.invoker.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rpc.Environment;
import com.rpc.annotation.Invoker.ServiceInvokerAutowired;
import com.rpc.annotation.Invoker.ServiceInvokerResource;
import com.rpc.bean.model.BeanDefinitionInfo;
import com.rpc.config.Config;
import com.rpc.invoker.HessianInvokerScanner;
import com.rpc.service.BeanFactoryPostProcessorService;
import com.rpc.util.BeanUtils;
import com.rpc.util.HessianUtil;
import com.rpc.util.ZKUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.*;

@Component
public class HessianInvokerScannerImpl implements HessianInvokerScanner , ApplicationContextAware{
    private static final Logger log= LoggerFactory.getLogger(HessianInvokerScannerImpl.class);

    private BeanFactoryPostProcessorService beanFactoryPostProcessorService;
    private ConfigurableListableBeanFactory configurableListableBeanFactory;
    private ApplicationContext applicationContext;

    private List<BeanDefinitionInfo> beanDefinitionInfoList=null;

    private Map<String , List<String>> springBeanProp = null;

    public static Map<String , List<String>> rpcObjectMap = new HashMap<String, List<String>>();

    private Config config;

    public void doInvoke() {
        this.beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
        this.configurableListableBeanFactory = beanFactoryPostProcessorService.configurableListableBeanFactory;
        this.config = applicationContext.getBean(Config.class);
        try {
            getBeanDefinition();
            invokeService();
            registerSpring();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取 bean 的注册信息 从当前项目的中的服务激活注解中获取
     * 需要注入的 属性的配置信息
     * @return
     * @throws Exception
     */
    public List<BeanDefinitionInfo> getBeanDefinition() throws Exception {
        beanDefinitionInfoList = Lists.newArrayList();
        springBeanProp = Maps.newHashMap();
        // 先获取 所有包含InvokeService 注解的属性
        String[] beanDefinitionNames = configurableListableBeanFactory.getBeanDefinitionNames();
        if(beanDefinitionNames != null && beanDefinitionNames.length>0) {
            for (int i= 0 ; i<beanDefinitionNames.length ; i++) {
                String beanName = beanDefinitionNames[i];
                Class clazz = applicationContext.getType(beanName);
                if(clazz != null) {
                    Field[] fields =clazz.getDeclaredFields();
                    for (Field field : fields) {
                        log.info(field.getName());
                        ServiceInvokerResource serviceInvokerResource = field.getAnnotation(ServiceInvokerResource.class);
                        ServiceInvokerAutowired serviceInvokerAutowired = field.getAnnotation(ServiceInvokerAutowired.class);
                        BeanDefinitionInfo beanDefinitionInfo = null;
                        if (serviceInvokerResource != null) {
                            beanDefinitionInfo = doDealInvokeResource(beanName , field, serviceInvokerResource);
                        }
                        if (serviceInvokerAutowired != null) {
                            beanDefinitionInfo = doDealInvokeAutowired(beanName , field, serviceInvokerAutowired);
                        }
                        if (beanDefinitionInfo != null) {
                            beanDefinitionInfo.setSpringBeanName( beanName );
                            beanDefinitionInfoList.add(beanDefinitionInfo);
                        }
                    }
                }
            }
        }
        return beanDefinitionInfoList;
    }

    /**
     * 暂存  关联
     * @param beanName
     * @param field
     */
//    private void addToRpcObjectMap(String beanName, Field field) {
//        List<String> propNames = null;
//        if(rpcObjectMap.get( beanName )!=null && rpcObjectMap.get( beanName ).size()>0){
//            propNames= rpcObjectMap.get(beanName);
//        }else {
//            propNames = new ArrayList<String>();
//        }
//        propNames.add( field.getType().getSimpleName());
//        rpcObjectMap.put(beanName, propNames);
//    }

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

    /**
     * 激活 对应环境的服务 获取服务的实例
     * 添加到bean BeanDefinitionInfo 中
     * @throws Exception
     */
    public void invokeService() throws Exception {
        if(CollectionUtils.isEmpty(beanDefinitionInfoList)){
            log.info(" empty List ");
            return;
        }
        for(BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoList){

            String nodeName = "/"+config.getProjectName()+"-"+beanDefinitionInfo.getEnvironment();
            nodeName+="/"+beanDefinitionInfo.getInterfaceClazz().getSimpleName();

            String jsonZKStr = ZKUtil.getNodeData(nodeName);
            if(jsonZKStr==null || jsonZKStr.length()==0){
                continue;
            }
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
            BeanUtils.copyProperties( beanDefinitionInfoRpc , beanDefinitionInfo ,true );

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

    /**
     * 注册到对应 spring bean 的属性中
     */
    public void registerSpring() {
        if(CollectionUtils.isEmpty(beanDefinitionInfoList)){
            log.info(" empty List ");
            return ;
        }
        for(BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoList){
            String propNameFinal = beanDefinitionInfo.getFiledName();
            String rpcBeanClassName = beanDefinitionInfo.getSpringBeanName();
            DefaultListableBeanFactory defaultListableBeanFactory = beanFactoryPostProcessorService.defaultListableBeanFactory;
            ScannedGenericBeanDefinition rpcBeanDefinition = (ScannedGenericBeanDefinition) defaultListableBeanFactory.getBeanDefinition(rpcBeanClassName);
            MutablePropertyValues pv =  rpcBeanDefinition.getPropertyValues();
            pv.addPropertyValue(propNameFinal , beanDefinitionInfo.getServiceObject());
            if(defaultListableBeanFactory.containsBeanDefinition(rpcBeanClassName)) {
                defaultListableBeanFactory.removeBeanDefinition(rpcBeanClassName);
            }
            defaultListableBeanFactory.registerBeanDefinition(rpcBeanClassName, rpcBeanDefinition);

//                    beanFactoryPostProcessorService.configurableListableBeanFactory.registerSingleton(propBeanName, beanDefinitionInfo.getServiceObject());
            log.info("注册bean" + beanDefinitionInfo.getInterfaceClazz().getName() + " 到 rpcBeanName 中的属性中");
        }
//        for (Iterator iterator = rpcObjectMap.keySet().iterator() ; iterator.hasNext();){
//            String rpcBeanClassName = (String) iterator.next();
//            List<String> propBeanNames = rpcObjectMap.get(rpcBeanClassName);
//            if(propBeanNames!=null && propBeanNames.size()>0){
//                for(String propBeanName : propBeanNames){
//                      BeanDefinitionInfo beanDefinitionInfo = interfaceNameMap.get(propBeanName);
//                }
//            }
//        }
    }

    public List<BeanDefinitionInfo> getBeanDefinitionInfoList() {
        return beanDefinitionInfoList;
    }

    public Map<String, List<String>> getRpcObjectMap() {
        return rpcObjectMap;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

