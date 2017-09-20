package com.rpc.bean.model;

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

    private String filedName;

    private String springBeanName;

    public String getSpringBeanName() {
        return springBeanName;
    }

    public void setSpringBeanName(String springBeanName) {
        this.springBeanName = springBeanName;
    }

    public String getFiledName() {
        return filedName;
    }

    public void setFiledName(String filedName) {
        this.filedName = filedName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getBeanInterfaceName() {
        return beanInterfaceName;
    }

    public void setBeanInterfaceName(String beanInterfaceName) {
        this.beanInterfaceName = beanInterfaceName;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Class getInterfaceClazz() {
        return interfaceClazz;
    }

    public void setInterfaceClazz(Class interfaceClazz) {
        this.interfaceClazz = interfaceClazz;
    }

    public Object getServiceObject() {
        return serviceObject;
    }

    public void setServiceObject(Object serviceObject) {
        this.serviceObject = serviceObject;
    }

    public Class getServiceClazz() {
        return serviceClazz;
    }

    public void setServiceClazz(Class serviceClazz) {
        this.serviceClazz = serviceClazz;
    }
}
