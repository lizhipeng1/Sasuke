package com.rpc.annotation.provider;

import com.rpc.enums.RpcTypeEnum;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.lang.annotation.*;

/**
 * rpc 服务提供方
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface ServiceProvider {
    String value() default "";

    RpcTypeEnum rpcTypeEnum() default RpcTypeEnum.Hessian;

}
