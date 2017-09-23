package com.rpc.invoker;

import com.rpc.bean.model.BeanDefinitionInfo;

import java.util.List;

/**
 * 服务激活通用接口，包括方法如下：
 * 1. 扫描所有的 带有rpc注解的类
 * 2. 根据参数（待定） 实施不同的rpc 服务 服务获取方法
 * 3. 注册到spring 容器其中
 */
public interface ServiceInvokerOperator {

    /**
     * 获取具体的服务
     * 需要做判断验证（待续）
     */
    void invokeService() throws Exception;

    /**
     * 注册bean到spring 中
     * @return
     */
    void registerSpring();

    /**
     *
     */
    void doInvoke(List<BeanDefinitionInfo> beanDefinitionInfoList);
}
