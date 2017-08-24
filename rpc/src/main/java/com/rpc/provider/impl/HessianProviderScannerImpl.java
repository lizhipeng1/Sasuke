package com.rpc.provider.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.rpc.Environment;
import com.rpc.annotation.provider.ServiceProvider;
import com.rpc.provider.HessianProviderScanner;
import com.rpc.bean.model.BeanDefinitionInfo;
import com.rpc.service.BeanFactoryPostProcessorService;
import com.rpc.util.ZKUtil;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class HessianProviderScannerImpl implements HessianProviderScanner, ApplicationContextAware{
    private static final Logger log= LoggerFactory.getLogger(HessianProviderScannerImpl.class);
    private ApplicationContext applicationContext;


    private  List<BeanDefinitionInfo> beanDefinitionInfoList;

    @Autowired
    private BeanFactoryPostProcessorService beanFactoryPostProcessorService;

//    @Value("${rpc.server.prefix}")
    private String rpcServerPrefix = "http://10.240.251.50:8188/order";


    @PostConstruct
    public void init(){
    }
    public void doProvider() throws  Exception {
        scannerBeanInfo();
        if(!CollectionUtils.isEmpty(beanDefinitionInfoList)) {
            publishRemote();
            registerManager();
        }else {
            log.info("未找到要发布的远程服务 跳过发布");
        }
    }


    public List<BeanDefinitionInfo> scannerBeanInfo() throws ClassNotFoundException {

        List<BeanDefinitionInfo> beanDefinitionInfos = Lists.newArrayList();
        Map<String , Object> beanMap  = beanFactoryPostProcessorService.configurableListableBeanFactory.getBeansWithAnnotation(ServiceProvider.class);

        if(CollectionUtils.isEmpty(beanMap)){
            return Lists.newArrayList();
        }
        String [] str = new String[]{};
        List<String> beanNames = Arrays.asList( beanMap.keySet().toArray(str) );

        for (String tem : beanNames) {
            log.info("spring bean Name  "+ tem);
            BeanDefinitionInfo beanDefinitionInfo = new BeanDefinitionInfo();
            BeanDefinition beanDefinition = beanFactoryPostProcessorService.configurableListableBeanFactory.getBeanDefinition(tem);
            String className = beanDefinition.getBeanClassName();
            Class<?> clazz;

            clazz = Class.forName(className);
            beanDefinitionInfo.setInterfaceClazz( clazz.getInterfaces()[0]  );
            beanDefinitionInfo.setServiceClazz(clazz);
            String parentName =clazz.getInterfaces()[0].getSimpleName();
            String beanName = parentName.substring(0,1).toLowerCase()+parentName.substring(1);
            String url = "/" +beanName;
            beanDefinitionInfo.setBeanInterfaceName( beanName );
            beanDefinitionInfo.setRequestUrl(  rpcServerPrefix+url  );

//            beanDefinitionInfo.setHessianUrl( url );

            beanDefinitionInfo.setEnvironment( Environment.environment);
            beanDefinitionInfo.setBeanName(tem);
            beanDefinitionInfos.add(beanDefinitionInfo);
        }
        beanDefinitionInfoList = beanDefinitionInfos;
        return beanDefinitionInfos;
    }

    public void publishRemote() throws  Exception {
        for (BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoList) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(HessianServiceExporter.class);
            builder.addPropertyReference("service", beanDefinitionInfo.getBeanName());
            builder.addPropertyValue("serviceInterface", beanDefinitionInfo.getInterfaceClazz());
            ((BeanDefinitionRegistry) beanFactoryPostProcessorService.configurableListableBeanFactory).registerBeanDefinition("/"+beanDefinitionInfo.getBeanInterfaceName(),
                    builder.getBeanDefinition());
        }
    }

    public void registerManager()  throws  Exception{
        for(BeanDefinitionInfo beanDefinitionInfo : beanDefinitionInfoList){
            String nodeName = "/"+Environment.environment +"-"+beanDefinitionInfo.getInterfaceClazz().getName().replace(".","-");

            CuratorFramework client = ZKUtil.getClient();
            client.create().forPath(nodeName , JSONObject.toJSONBytes(beanDefinitionInfo));

//            ZKUtil.createNodeWithData( nodeName , beanDefinitionInfo);
//            redisServiceRpc.set(Environment.environment +"-"+beanDefinitionInfo.getInterfaceClazz().getName() , JSONObject.toJSONString( beanDefinitionInfo ));
        }

    }



    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
