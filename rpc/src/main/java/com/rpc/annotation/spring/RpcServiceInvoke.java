package com.rpc.annotation.spring;

import org.springframework.context.annotation.Lazy;

import java.lang.annotation.*;

/**
 * rpc 服务提供方
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@org.springframework.stereotype.Component
@Lazy
public @interface RpcServiceInvoke {
}
