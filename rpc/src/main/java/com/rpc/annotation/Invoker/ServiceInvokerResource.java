package com.rpc.annotation.Invoker;

import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.lang.annotation.*;

/**
 * rpc 服务调用方
 * 通过Resource 自动注入可以指定环境参数
 * 可以根据名称匹配
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Lazy
public @interface ServiceInvokerResource {
    String value() default "dev";
}
