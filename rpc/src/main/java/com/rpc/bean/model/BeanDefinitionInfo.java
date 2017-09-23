package com.rpc.bean.model;

import com.rpc.enums.RpcTypeEnum;

import java.io.Serializable;

/**
 * 存储 服务bean的相关信息
 */
public class BeanDefinitionInfo implements Serializable {
    private String requestUrl;  //  请求地址

    private String beanInterfaceName;   // 注册的bean name 默认是 接口service 名称首字母小写

    private String beanName;   // 注册的bean name 默认是 接口service 名称首字母小写

    private Class interfaceClazz;       // 注册的 接口 的 bean class

    private Class serviceClazz;       // 注册的 服务的 bean class

    private Object serviceObject; // 服务对应的具体的对象

    private String environment; //环境 dev qa pro

    private String filedName;   // 需要 动态注入的字段名

    private String springBeanName;  // spring beanName

    private RpcTypeEnum rpcTypeEnum;    // 当前的bean 使用哪种方法暴露rpc 服务

    public RpcTypeEnum getRpcTypeEnum() {
        return rpcTypeEnum;
    }

    public BeanDefinitionInfo setRpcTypeEnum(RpcTypeEnum rpcTypeEnum) {
        this.rpcTypeEnum = rpcTypeEnum;
        return this;
    }

    public String getSpringBeanName() {
        return springBeanName;
    }

    public BeanDefinitionInfo setSpringBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
        return this;
    }

    public String getFiledName() {
        return filedName;
    }

    public BeanDefinitionInfo setFiledName(String filedName) {
        this.filedName = filedName;
        return this;
    }

    public String getEnvironment() {
        return environment;
    }

    public BeanDefinitionInfo setEnvironment(String environment) {
        this.environment = environment;
        return this;
    }

    public String getBeanInterfaceName() {
        return beanInterfaceName;
    }

    public BeanDefinitionInfo setBeanInterfaceName(String beanInterfaceName) {
        this.beanInterfaceName = beanInterfaceName;
        return this;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public BeanDefinitionInfo setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    public String getBeanName() {
        return beanName;
    }

    public BeanDefinitionInfo setBeanName(String beanName) {
        this.beanName = beanName;
        return this;
    }

    public Class getInterfaceClazz() {
        return interfaceClazz;
    }

    public BeanDefinitionInfo setInterfaceClazz(Class interfaceClazz) {
        this.interfaceClazz = interfaceClazz;
        return this;
    }

    public Object getServiceObject() {
        return serviceObject;
    }

    public BeanDefinitionInfo setServiceObject(Object serviceObject) {
        this.serviceObject = serviceObject;
        return this;
    }

    public Class getServiceClazz() {
        return serviceClazz;
    }

    public BeanDefinitionInfo setServiceClazz(Class serviceClazz) {
        this.serviceClazz = serviceClazz;
        return this;
    }
}
