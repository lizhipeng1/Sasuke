package com.rpc.invoker.impl;

import com.alibaba.fastjson.JSONObject;
import com.rpc.Environment;
import com.rpc.bean.model.BeanDefinitionInfo;
import com.rpc.exec.ThreadExecutor;
import com.rpc.exec.ThreadRunnable;
import com.rpc.invoker.HessianInvokerOperator;
import com.rpc.service.BeanFactoryPostProcessorService;
import com.rpc.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class HessianInvokerOperatorImpl implements HessianInvokerOperator, ApplicationContextAware {
    private static final Logger log= LoggerFactory.getLogger(HessianInvokerOperatorImpl.class);
    private ApplicationContext applicationContext;

    private BeanFactoryPostProcessorService beanFactoryPostProcessorService;

    private DefaultListableBeanFactory defaultListableBeanFactory;

    private List<BeanDefinitionInfo> beanDefinitionInfoListCopyOnWrite = new CopyOnWriteArrayList<BeanDefinitionInfo>();

    private ZKUtil zkUtil;

    private Map<String , List<BeanDefinitionInfo>> beanMap = null;

    public void doInvoke(List<BeanDefinitionInfo> beanDefinitionInfoList ) {
        this.beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
        defaultListableBeanFactory = beanFactoryPostProcessorService.getDefaultListableBeanFactory();
        if(!CollectionUtils.isEmpty(beanDefinitionInfoList)){
            beanDefinitionInfoListCopyOnWrite.addAll( beanDefinitionInfoList );
            doInvokeZkServiceToSpring(beanDefinitionInfoListCopyOnWrite);

            beanMap = Utils.list2MapList(beanDefinitionInfoListCopyOnWrite, new Utils.KeyGenerator<BeanDefinitionInfo>() {
                @Override
                public String generate(BeanDefinitionInfo beanDefinitionInfo) {
                    return beanDefinitionInfo.getSpringBeanName();
                }
            });
        }
    }

    /**
     * 激活 对应环境的服务 获取服务的实例
     * 添加到bean BeanDefinitionInfo 中
     * @throws Exception
     */
    public void invokeService() {
        while ( beanDefinitionInfoListCopyOnWrite!=null && beanDefinitionInfoListCopyOnWrite.size()>0) {
            for (BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoListCopyOnWrite) {
                invokeService(null , beanDefinitionInfo);
                beanDefinitionInfoListCopyOnWrite.remove( beanDefinitionInfo );
            }
        }

    }

    @Override
    public void invokeService(Object object ,String springBeanName) {
        if(org.apache.commons.lang3.StringUtils.isNotBlank(springBeanName) && beanMap!=null && beanMap.size()>0 && beanMap.containsKey(springBeanName)){
            List<BeanDefinitionInfo> beanDefinitionInfoList = beanMap.get(springBeanName);
            for(BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoList){
                invokeService( ReflectionUtils.getTarget(object) , beanDefinitionInfo );
            }
        }
    }


    private void  invokeService(Object objectParam , BeanDefinitionInfo beanDefinitionInfo ){
        if (beanDefinitionInfo.getServiceObject() != null) {
            Object object =  objectParam == null ? applicationContext.getBean(beanDefinitionInfo.getSpringBeanName()) : objectParam;
            if(ReflectionUtils.getFieldValue( object , beanDefinitionInfo.getFiledName()) == null ){
                ReflectionUtils.setFieldValue( object , beanDefinitionInfo.getFiledName(), beanDefinitionInfo.getServiceObject());
                log.info("添加 ：" + beanDefinitionInfo.getEnvironment() + " 下的服务 " + beanDefinitionInfo.getInterfaceClazz().getName() +
                        " 到 ：" + beanDefinitionInfo.getServiceObject().getClass().getSimpleName());
            }else {
                log.info(  beanDefinitionInfo.getEnvironment() + " 环境下的服务 " + beanDefinitionInfo.getInterfaceClazz().getName() +
                        " 已存在于 " + beanDefinitionInfo.getServiceObject().getClass().getSimpleName());
            }

        }
    }

    /**
     * 将 bean 的定义信息 注入到 对应的 service controller 中
     * @param beanDefinitionInfoListCopyOnWrite
     */
    private void doInvokeZkServiceToSpring(List<BeanDefinitionInfo> beanDefinitionInfoListCopyOnWrite) {
        for (BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoListCopyOnWrite) {

            String nodeName = ServiceZKNodeNameUtil.getServiceZKNodeName(beanDefinitionInfo.getEnvironment(),
                    beanDefinitionInfo.getInterfaceClazz().getName());
            if (zkUtil.exitNode(nodeName)) {
                String jsonZKStr = zkUtil.getNodeData(nodeName);
                if (jsonZKStr == null || jsonZKStr.length() == 0) {
                    log.info("未找到环境：" + beanDefinitionInfo.getEnvironment() + " 下的服务 ：" + beanDefinitionInfo.getInterfaceClazz().getName());
                    continue;
                }
                String beanDefinitionStr = JSONObject.parse(jsonZKStr).toString();

                BeanDefinitionInfo beanDefinitionInfoRpc = JSONObject.parseObject(beanDefinitionStr, BeanDefinitionInfo.class);

                if (beanDefinitionInfoRpc == null || StringUtils.isEmpty(beanDefinitionInfoRpc.getRequestUrl())) {
                    log.info("未找到环境：" + beanDefinitionInfo.getEnvironment() + " 下的服务 ：" + beanDefinitionInfo.getInterfaceClazz().getName());
                    continue;
                }
                BeanUtils.copyProperties(beanDefinitionInfoRpc, beanDefinitionInfo, true);
                if ( ! defaultListableBeanFactory.containsBeanDefinition(beanDefinitionInfo.getBeanName()) ) {
                    if( !defaultListableBeanFactory.containsSingleton(getBeanName(beanDefinitionInfo)) ){
                        try {
                            Object hessianObject = HessianUtil.factory.create(beanDefinitionInfo.getInterfaceClazz(), beanDefinitionInfo.getRequestUrl());
                            beanDefinitionInfo.setServiceObject(hessianObject);
                            defaultListableBeanFactory.registerSingleton(getBeanName(beanDefinitionInfo), hessianObject);
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                            log.info("获取 bean " + beanDefinitionInfo.getInterfaceClazz().getName() + " 出现异常 " + e.toString());
                            continue;
                        }
                    }else {
                        beanDefinitionInfo.setServiceObject(defaultListableBeanFactory.getBean(getBeanName(beanDefinitionInfo)));

                    }
                } else {
                    beanDefinitionInfo.setServiceObject(defaultListableBeanFactory.getBean( beanDefinitionInfo.getBeanName() ));
                }
                // 直接注入到属性中 不可以直接反射注入 会出现提前实例化大致 导致属性无法自动注入的情况
//                invokeService(beanDefinitionInfo);

            } else {
                log.info( beanDefinitionInfo.getEnvironment() + " 下的服务 ：" + beanDefinitionInfo.getInterfaceClazz().getName() +"不存在");
            }
        }
    }

    /**
     * 获取 springname  name-environment
     * @param beanDefinitionInfo
     * @return
     */
    private String getBeanName(BeanDefinitionInfo beanDefinitionInfo) {
        return beanDefinitionInfo.getBeanName()+"-"+beanDefinitionInfo.getEnvironment();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        zkUtil = this.applicationContext.getBean(ZKUtil.class);
    }

    @Override
    public void shutDown() {
        beanDefinitionInfoListCopyOnWrite.clear();
    }

    @Override
    public void run() {
        // 暂时未用到
    }
}

