package com.rpc.monitor.model;

import java.io.Serializable;
import java.util.Date;

public class ServiceInfo implements Serializable {
    private Integer id;

    private String beanInterfaceName;   // 注册的bean name 默认是 接口service 名称首字母小写

    private String beanName;    // 注册的bean name 默认是 接口service 名称首字母小写

    private String interfaceClazz;   // 注册的 接口 的 bean class

    private String requestUrl;    //  请求地址

    private String rpcTypeEnum; // 当前的bean 使用哪种方法暴露rpc 服务

    private String serviceClazz;     // 注册的 服务的 bean class

    private String environment;  //环境 dev qa pro

    private Integer alive;  // 是否存活

    private Date createDatetime;    // 创建时间

    private Date modifyDatetime;    // 更新时间


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBeanInterfaceName() {
        return beanInterfaceName;
    }

    public void setBeanInterfaceName(String beanInterfaceName) {
        this.beanInterfaceName = beanInterfaceName == null ? null : beanInterfaceName.trim();
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName == null ? null : beanName.trim();
    }

    public String getInterfaceClazz() {
        return interfaceClazz;
    }

    public void setInterfaceClazz(String interfaceClazz) {
        this.interfaceClazz = interfaceClazz == null ? null : interfaceClazz.trim();
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl == null ? null : requestUrl.trim();
    }

    public String getRpcTypeEnum() {
        return rpcTypeEnum;
    }

    public void setRpcTypeEnum(String rpcTypeEnum) {
        this.rpcTypeEnum = rpcTypeEnum == null ? null : rpcTypeEnum.trim();
    }

    public String getServiceClazz() {
        return serviceClazz;
    }

    public void setServiceClazz(String serviceClazz) {
        this.serviceClazz = serviceClazz == null ? null : serviceClazz.trim();
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment == null ? null : environment.trim();
    }

    public Integer getAlive() {
        return alive;
    }

    public void setAlive(Integer alive) {
        this.alive = alive;
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public Date getModifyDatetime() {
        return modifyDatetime;
    }

    public void setModifyDatetime(Date modifyDatetime) {
        this.modifyDatetime = modifyDatetime;
    }
}