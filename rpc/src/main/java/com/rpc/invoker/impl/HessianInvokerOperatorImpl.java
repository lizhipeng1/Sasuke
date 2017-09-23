package com.rpc.invoker.impl;

import com.alibaba.fastjson.JSONObject;
import com.rpc.Environment;
import com.rpc.bean.model.BeanDefinitionInfo;
import com.rpc.config.Config;
import com.rpc.invoker.HessianInvokerOperator;
import com.rpc.service.BeanFactoryPostProcessorService;
import com.rpc.util.BeanUtils;
import com.rpc.util.HessianUtil;
import com.rpc.util.ZKUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.ScannedGenericBeanDefinition;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.util.*;

@Component
public class HessianInvokerOperatorImpl implements HessianInvokerOperator, ApplicationContextAware{
    private static final Logger log= LoggerFactory.getLogger(HessianInvokerOperatorImpl.class);

    private BeanFactoryPostProcessorService beanFactoryPostProcessorService;
    private ApplicationContext applicationContext;

    private List<BeanDefinitionInfo> beanDefinitionInfoList=null;

    private Map<String , List<String>> springBeanProp = null;

    public static Map<String , List<String>> rpcObjectMap = new HashMap<String, List<String>>();

    private Config config;

    public void doInvoke( List<BeanDefinitionInfo> beanDefinitionInfoList ) {
        this.beanFactoryPostProcessorService = applicationContext.getBean(BeanFactoryPostProcessorService.class);
        this.config = applicationContext.getBean(Config.class);
        if(!CollectionUtils.isEmpty(beanDefinitionInfoList)){
            this.beanDefinitionInfoList = beanDefinitionInfoList;
            try {
                invokeService();
                registerSpring();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
            log.info("注册bean" + beanDefinitionInfo.getInterfaceClazz().getName() + " 到 "+rpcBeanClassName+" 中的属性中");
        }
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

