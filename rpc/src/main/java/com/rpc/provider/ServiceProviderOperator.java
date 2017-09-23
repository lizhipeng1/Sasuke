package com.rpc.provider;

import com.rpc.bean.model.BeanDefinitionInfo;

import java.net.UnknownHostException;
import java.util.List;

/**
 * 服务扫面通用接口，包括方法如下：
 * 1. 扫描所有的 带注解的类
 * 2. 根据参数（待定） 实施不同的额rpc 服务暴露方法
 * 3. 注册到zk  redis
 */
public interface ServiceProviderOperator {
    /**
     * 发布服务到远程
     */
      void publishRemote() throws Exception;

    /**
     * 注册到 持久化管理软件中 如ZK
     */
      void registerManager() throws UnknownHostException, Exception;

    /**
     * 处理发布
     */
      void doProvider(List<BeanDefinitionInfo> beanDefinitionInfoList);

}
