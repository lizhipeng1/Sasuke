package com.rpc.service;

import com.rpc.invoker.impl.HessianInvokerOperatorImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ApplicationListenerService implements ApplicationListener<ContextRefreshedEvent>{
    @Autowired
    HessianInvokerOperatorImpl hessianInvokerScannerImpl;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
    }
}
