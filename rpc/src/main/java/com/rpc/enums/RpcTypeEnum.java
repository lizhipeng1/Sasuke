package com.rpc.enums;

import com.rpc.invoker.DubboInvokerOperator;
import com.rpc.invoker.HessianInvokerOperator;
import com.rpc.invoker.RMIInvokerOperator;
import com.rpc.provider.DubboProviderOperator;
import com.rpc.provider.HessianProviderOperator;
import com.rpc.provider.RMIProviderOperator;

/**
 * 枚举类 dubbo RMI hessian
 */
public enum RpcTypeEnum {

    Hessian("hessian" , HessianProviderOperator.class , HessianInvokerOperator.class),
    RMI("RMI" , RMIProviderOperator.class , RMIInvokerOperator.class),
    BRPC("baiduRPC" , null  , null),
    Dubbo("dubbo" , DubboProviderOperator.class , DubboInvokerOperator.class);

    private String name;
    private Class providerClazz;
    private Class invokeClazz;

    RpcTypeEnum(String name, Class providerClazz, Class invokeClazz) {
        this.name = name;
        this.providerClazz = providerClazz;
        this.invokeClazz = invokeClazz;
    }

    public String getName() {
        return name;
    }

    public Class getProviderClazz() {
        return providerClazz;
    }

    public Class getInvokeClazz() {
        return invokeClazz;
    }
}
