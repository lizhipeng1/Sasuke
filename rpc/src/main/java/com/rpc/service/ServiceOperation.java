package com.rpc.service;

import com.rpc.provider.HessianProviderScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * 服务 发布类
 */
@Component
public class ServiceOperation {
    private static final Logger log= LoggerFactory.getLogger(ServiceOperation.class);
    @Autowired
    private HessianProviderScanner hessianProviderScanner;

    @PostConstruct
    public void init(){
        try {
            log.info(" 执行扫描 Provider 服务发布 ");
            hessianProviderScanner.doProvider();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
