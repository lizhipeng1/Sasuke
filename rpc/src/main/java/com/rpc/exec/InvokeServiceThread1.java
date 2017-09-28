package com.rpc.exec;

import com.alibaba.fastjson.JSONObject;
import com.rpc.Environment;
import com.rpc.bean.model.BeanDefinitionInfo;
import com.rpc.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class InvokeServiceThread1 implements ThreadRunnable , ApplicationContextAware {

    private static final Logger log= LoggerFactory.getLogger(InvokeServiceThread1.class);


    private List<BeanDefinitionInfo> beanDefinitionInfoListCopyOnWrite = new CopyOnWriteArrayList<BeanDefinitionInfo>();

    private List<BeanDefinitionInfo> beanDefinitionInfoList;

    private  ApplicationContext applicationContext;


    public InvokeServiceThread1() {
    }


    public List<BeanDefinitionInfo> getBeanDefinitionInfoList() {
        return beanDefinitionInfoList;
    }

    public void setBeanDefinitionInfoList(List<BeanDefinitionInfo> beanDefinitionInfoList) {
        this.beanDefinitionInfoList = beanDefinitionInfoList ;
    }

    @Override
    public void run() {
        int i=0;
        this.beanDefinitionInfoListCopyOnWrite.addAll( beanDefinitionInfoList );
        while ( !beanDefinitionInfoListCopyOnWrite.isEmpty() ) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("执行 runInvoke "+(i++) );
            for (BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoListCopyOnWrite) {
                Object object = applicationContext.getBean(beanDefinitionInfo.getSpringBeanName());
                if(ReflectionUtils.getFieldValue( object , beanDefinitionInfo.getFiledName() )== null){
                    String nodeName = ServiceZKNodeNameUtil.getServiceZKNodeName(beanDefinitionInfo.getEnvironment(),
                            beanDefinitionInfo.getInterfaceClazz().getName());
                    if (ZKUtil.exitNode(nodeName)) {
                        String jsonZKStr = ZKUtil.getNodeData(nodeName);
                        if (jsonZKStr == null || jsonZKStr.length() == 0) {
                            continue;
                        }
                        String beanDefinitionStr = JSONObject.parse(jsonZKStr).toString();

                        if (StringUtils.isEmpty(beanDefinitionStr)) {
                            log.info("未找到环境：" + beanDefinitionInfo.getEnvironment() + " 下的服务 ：" + beanDefinitionInfo.getInterfaceClazz().getName());
                            continue;
                        }
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
                        beanDefinitionInfoListCopyOnWrite.remove( beanDefinitionInfo );
                    }
                }
            }
        }
    }

    @Override
    public void shutDown() {

    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
