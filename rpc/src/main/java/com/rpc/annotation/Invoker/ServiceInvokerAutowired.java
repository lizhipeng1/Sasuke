package com.rpc.annotation.Invoker;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import javax.annotation.Resource;
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
@Lazy
public @interface ServiceInvokerAutowired {

}
