package com.rpc.service;

import com.rpc.invoker.impl.HessianInvokerScannerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

public class ApplicationListenerService implements ApplicationListener<ContextRefreshedEvent>{
    @Autowired
    HessianInvokerScannerImpl hessianInvokerScannerImpl;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(event.getApplicationContext().getParent() == null) {
            try {
                hessianInvokerScannerImpl.doInvoke();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
