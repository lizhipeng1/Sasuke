package com.rpc.service;

import com.rpc.invoker.impl.HessianInvokerScannerImpl;
import com.rpc.lock.ServiceRunable;
import com.rpc.provider.HessianProviderScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by hzlizhipeng on 2017/8/11.
 */
@Component
public class ServiceOperation {
    private static final Logger log= LoggerFactory.getLogger(ServiceOperation.class);

    ExecutorService fixedThreadPool = Executors.newFixedThreadPool(3);

    private Lock lock = new ReentrantLock();

    private Condition provideCondition = lock.newCondition();

    private Condition invokeCondition = lock.newCondition();


    @Autowired
    private HessianInvokerScannerImpl hessianInvokerScanner;
    @Autowired
    private HessianProviderScanner hessianProviderScanner;

    @PostConstruct
    public void init(){
        fixedThreadPool.execute(new ServiceRunable(   ) {
            public void run() {
                lock.lock();
                log.info(" 执行扫描 Provider 服务发布 ");
                try {
                    hessianProviderScanner.doProvider();
                    log.info(" 执行扫描 Invoker  服务订阅服务 ");
                    hessianInvokerScanner.doInvoke();
                } catch (Exception e) {
                   e.printStackTrace();
                }finally {
                    invokeCondition.signal();
                    lock.unlock();
                }


            }
        });
        fixedThreadPool.execute(new ServiceRunable() {
            public void run() {
                try {
                    lock.lock();
                    invokeCondition.await();
                    log.info(" 执行扫描 Invoker  服务订阅服务 ");
                    hessianInvokerScanner.doInvoke();
                } catch (InterruptedException e) {
                    log.info(e.toString());
                } catch (Exception e) {
                    log.info(e.toString());
                } finally {
                    lock.unlock();
                }


            }
        });
    }
}
