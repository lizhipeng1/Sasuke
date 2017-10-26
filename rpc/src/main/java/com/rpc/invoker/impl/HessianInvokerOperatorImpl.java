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
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class HessianInvokerOperatorImpl implements HessianInvokerOperator, ApplicationContextAware {
    private static final Logger log= LoggerFactory.getLogger(HessianInvokerOperatorImpl.class);
    private ApplicationContext applicationContext;

    private List<BeanDefinitionInfo> beanDefinitionInfoListCopyOnWrite = new CopyOnWriteArrayList<BeanDefinitionInfo>();
    public ThreadExecutor threadExecutor;

    public void doInvoke( List<BeanDefinitionInfo> beanDefinitionInfoList ) {
        this.threadExecutor = applicationContext.getBean(ThreadExecutor.class);
        if(!CollectionUtils.isEmpty(beanDefinitionInfoList)){
            beanDefinitionInfoListCopyOnWrite.addAll( beanDefinitionInfoList );
        }
    }

    /**
     * 激活 对应环境的服务 获取服务的实例
     * 添加到bean BeanDefinitionInfo 中
     * @throws Exception
     */
    public void invokeService() {
        /*
            1. 每次循环会 动态移除已经添加的服务
         */
        while (beanDefinitionInfoListCopyOnWrite!=null && beanDefinitionInfoListCopyOnWrite.size()>0){
            doInvokeZkService(beanDefinitionInfoListCopyOnWrite);
        }

    }

    /**
     * 将 bean 的定义信息 注入到 对应的 service controller 中
     * @param beanDefinitionInfoListCopyOnWrite
     */
    private void doInvokeZkService(List<BeanDefinitionInfo> beanDefinitionInfoListCopyOnWrite) {
        for (BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoListCopyOnWrite) {
            String nodeName = ServiceZKNodeNameUtil.getServiceZKNodeName(beanDefinitionInfo.getEnvironment(),
                    beanDefinitionInfo.getInterfaceClazz().getName());
            if (ZKUtil.exitNode(nodeName)) {
                Object object = applicationContext.getBean(beanDefinitionInfo.getSpringBeanName());
                if (ReflectionUtils.getFieldValue(object, beanDefinitionInfo.getFiledName()) == null) {
                    String jsonZKStr = ZKUtil.getNodeData(nodeName);
                    if (jsonZKStr == null || jsonZKStr.length() == 0) {
                        log.info("未找到环境：" + beanDefinitionInfo.getEnvironment() + " 下的服务 ：" + beanDefinitionInfo.getInterfaceClazz().getName());
                        continue;
                    }
                    String beanDefinitionStr = JSONObject.parse(jsonZKStr).toString();

                    BeanDefinitionInfo beanDefinitionInfoRpc = JSONObject.parseObject(beanDefinitionStr, BeanDefinitionInfo.class);
                    if (beanDefinitionInfoRpc == null) {
                        log.info("未找到环境：" + beanDefinitionInfo.getEnvironment() + " 下的服务 ：" + beanDefinitionInfo.getInterfaceClazz().getName());
                        continue;
                    }
                    BeanUtils.copyProperties(beanDefinitionInfoRpc, beanDefinitionInfo, true);

                    if (beanDefinitionInfo.getServiceClazz() != null && applicationContext.getBean(beanDefinitionInfo.getServiceClazz()) != null
                            && Environment.environment.equals(beanDefinitionInfo.getEnvironment())) {
                        log.info("当前容器中已经存在 环境为：" + beanDefinitionInfo.getEnvironment() + "的 bean实例：" + beanDefinitionInfo.getServiceClazz().getName() + " 不在注册 rpc 服务bean,使用本容器中的bean实例");
                        continue;
                    }

                    try {
                        beanDefinitionInfo.setServiceObject(HessianUtil.factory.create(beanDefinitionInfo.getInterfaceClazz(), beanDefinitionInfo.getRequestUrl()));
                    } catch (MalformedURLException e) {
                        log.info("获取 bean " + beanDefinitionInfo.getInterfaceClazz().getName() + " 出现异常 " + e.toString());
                        continue;
                    }

                    ReflectionUtils.setFieldValue(object, beanDefinitionInfo.getFiledName(), beanDefinitionInfo.getServiceObject());
                    log.info("添加 ：" + beanDefinitionInfo.getEnvironment() + " 下的服务 " + beanDefinitionInfo.getInterfaceClazz().getName() + " 到 ：" + object.getClass().getSimpleName());

                }
                beanDefinitionInfoListCopyOnWrite.remove(beanDefinitionInfo); // 成功以后移除 服务信息
            } else {
                log.info("未找到环境：" + beanDefinitionInfo.getEnvironment() + " 下的服务 ：" + beanDefinitionInfo.getInterfaceClazz().getName());
            }
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void shutDown() {
        beanDefinitionInfoListCopyOnWrite.clear();
    }

    @Override
    public void run() {
        invokeService();
    }
}

