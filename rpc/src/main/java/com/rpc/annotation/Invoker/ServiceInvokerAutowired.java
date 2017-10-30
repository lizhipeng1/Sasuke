package com.rpc.annotation.Invoker;

import com.rpc.enums.RpcTypeEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * rpc 服务调用方
 * 通过 Autowire 自动注入 根据当前系统的环境的类型匹配
 * 不可以指定环境参数
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Autowired
public @interface ServiceInvokerAutowired {

    RpcTypeEnum rpcTypeEnum() default RpcTypeEnum.Hessian;
}
