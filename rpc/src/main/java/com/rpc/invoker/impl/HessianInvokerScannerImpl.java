package com.rpc.invoker.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.rpc.Environment;
import com.rpc.annotation.Invoker.ServiceInvoker;
import com.rpc.annotation.spring.RpcServiceInvoke;
import com.rpc.config.Config;
import com.rpc.invoker.HessianInvokerScanner;
import com.rpc.bean.model.BeanDefinitionInfo;
import com.rpc.service.BeanFactoryPostProcessorService;
import com.rpc.util.HessianUtil;
import com.rpc.util.ReflectionUtil;
import com.rpc.util.ZKUtil;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Component
public class HessianInvokerScannerImpl implements HessianInvokerScanner,ApplicationContextAware {
    private static final Logger log= LoggerFactory.getLogger(HessianInvokerScannerImpl.class);

    @Autowired
    private BeanFactoryPostProcessorService beanFactoryPostProcessorService;


    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    private ApplicationContext applicationContext;

    private List<BeanDefinitionInfo> beanDefinitionInfoList=null;

    private Map<String , BeanDefinitionInfo> interfaceNameMap;

    @Autowired
    private Config config;

    @PostConstruct
    public void init() {
        configurableListableBeanFactory = beanFactoryPostProcessorService.configurableListableBeanFactory;
    }


    public void doInvoke() throws Exception {
        getBeanDefinition();
        invokeService();
        registerSpring();
    }

    public List<BeanDefinitionInfo> getBeanDefinition() throws Exception {
        beanDefinitionInfoList = Lists.newArrayList();
        interfaceNameMap = Maps.newHashMap();
        // 先获取 所有包含InvokeService 注解的属性
        Map<String , Object> beanMaps = configurableListableBeanFactory.getBeansWithAnnotation(RpcServiceInvoke.class);
        if(beanMaps != null && beanMaps.size()>0) {
            for (Iterator iterator = beanMaps.keySet().iterator(); iterator.hasNext(); ) {
                String beanName = (String) iterator.next();
                Object proxy = beanMaps.get(beanName);
                Object object = ReflectionUtil.getTarget( proxy );
                Field[] fields =object.getClass().getDeclaredFields();
                for (Field field : fields) {
                    log.info(field.getName());
                    ServiceInvoker serviceInvoker = field.getAnnotation(ServiceInvoker.class);
                    if (serviceInvoker == null) {
                        continue;
                    }
                    Class propClazz = field.getType();
                    if (interfaceNameMap.containsKey(propClazz.getName())) {
                        BeanDefinitionInfo tempBeanDefinitionInfo = interfaceNameMap.get(propClazz.getName());
                        if (tempBeanDefinitionInfo.getEnvironment().equals(serviceInvoker.value())) {
                            continue;
                        }
                    }
                    BeanDefinitionInfo beanDefinitionInfo = new BeanDefinitionInfo();
                    beanDefinitionInfo.setBeanName(field.getName());
                    beanDefinitionInfo.setInterfaceClazz(propClazz);
                    beanDefinitionInfo.setEnvironment(StringUtils.isEmpty(serviceInvoker.value()) ? Environment.environment : serviceInvoker.value());
                    beanDefinitionInfoList.add(beanDefinitionInfo);
                    interfaceNameMap.put(propClazz.getName(), beanDefinitionInfo);
                }
            }
        }
        return beanDefinitionInfoList;
    }

    public void invokeService() throws Exception {
        if(CollectionUtils.isEmpty(beanDefinitionInfoList)){
            log.info(" empty List ");
            return;
        }
        for(BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoList){

            String nodeName = "/"+config.getProjectName()+"-"+Environment.environment;
            nodeName+="/"+beanDefinitionInfo.getInterfaceClazz().getSimpleName();

            String  beanDefinitionStr  =   JSONObject.parse(ZKUtil.getNodeData(nodeName)).toString();

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

            //判断 在容器中是不是已经存在
            if( beanDefinitionInfo.getServiceClazz()!=null &&  this.applicationContext.getBean( beanDefinitionInfo.getServiceClazz() ) != null  ){
                log.info("当前容器中已经存在 " + beanDefinitionInfo.getServiceClazz().getName()  + "RpcServiceInvoke Bean  不在注册 rpc 服务bean");
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

    public List<String> registerSpring() {
        if(CollectionUtils.isEmpty(beanDefinitionInfoList)){
            log.info(" empty List ");
            return null;
        }
        List<String> backStr = Lists.newArrayList();
        for(BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoList) {
            backStr.add(beanDefinitionInfo.getBeanName());
            if (beanDefinitionInfo.getServiceObject() != null) {
                beanFactoryPostProcessorService.configurableListableBeanFactory.registerSingleton(beanDefinitionInfo.getBeanInterfaceName(), beanDefinitionInfo.getServiceObject());
                log.info("注册bean" + beanDefinitionInfo.getInterfaceClazz().getName() + " 到spring 容器中");
            }
        }
        return  backStr;
    }



    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

