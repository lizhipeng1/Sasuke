package com.rpc.load;

import com.rpc.invoker.HessianInvokerOperator;
import org.springframework.beans.BeansException;
import org.springframework.cglib.proxy.LazyLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Created by hzlizhipeng on 2017/10/31.
 */
public class ConcreteClassLazyLoader implements LazyLoader , ApplicationContextAware{

    private ApplicationContext applicationContext;

    private HessianInvokerOperator hessianInvokerOperator;

    @Override
    public Object loadObject() throws Exception {
        System.out.println("回到loadObject 方法");
        return null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext =applicationContext;
        this.hessianInvokerOperator = applicationContext.getBean(HessianInvokerOperator.class);
    }
}
