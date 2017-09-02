package com.rpc.service;

import com.rpc.invoker.impl.HessianInvokerScannerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Created by hzlizhipeng on 2017/9/2.
 */
//public class ApplicationListenerService implements ApplicationListener<ContextRefreshedEvent>{
//    @Autowired
//    HessianInvokerScannerImpl hessianInvokerScannerImpl;
//
//    @Override
//    public void onApplicationEvent(ContextRefreshedEvent event) {
//        if(event.getApplicationContext().getParent() == null) {
//            try {
//                hessianInvokerScannerImpl.doInvoke();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }
//}
