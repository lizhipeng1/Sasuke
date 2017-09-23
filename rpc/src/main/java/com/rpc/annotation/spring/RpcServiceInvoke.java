package com.rpc.annotation.spring;

import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * rpc 服务提供方
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface RpcServiceInvoke {
}
