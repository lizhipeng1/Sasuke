package com.rpc.invoker;


/**
 * 服务扫面通用接口，包括方法如下：
 * 1. 扫描所有的 带注解的类
 * 2. 根据参数（待定） 实施不同的额rpc 服务暴露方法
 * 3. 注册到zk  redis
 */
public interface ServiceInvokeScanner {
    /**
     * 扫描bean  生成beanDefinitionInfo
     * 分派 不同的 rpc 服务的暴露
     * @return
     */
    void scannerAllocateBeanInfo();

}
