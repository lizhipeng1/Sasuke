package com.rpc.annotation.Invoker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
import java.lang.annotation.*;

/**
 * rpc 服务调用方
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Resource
@Lazy
public @interface ServiceInvoker {
    String value() default "dev";
}
