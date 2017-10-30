package com.track.util;

import com.alibaba.fastjson.JSONObject;
import com.rpc.annotation.Invoker.ServiceInvokerAutowired;
import com.rpc.annotation.Invoker.ServiceInvokerResource;
import com.track.model.TrackPointInfo;
import com.track.service.TrackPointService;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TrackPointInterceptor implements MethodInterceptor {

    private static final Logger log= LoggerFactory.getLogger(TrackPointInterceptor.class);

    private ExecutorService threadPool = Executors.newFixedThreadPool(10);


    @ServiceInvokerResource
    private TrackPointService trackPointService;


    @Override
    public Object invoke(MethodInvocation methodInvocation){
        final String param = JSONObject.toJSONString( methodInvocation.getArguments());
        final String method = methodInvocation.getMethod().getName();
        String exception = null;

        final StopWatch clock = new StopWatch();
        clock.start(); //计时开始
        Object result = null;
        try {
            result = methodInvocation.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            exception = throwable.toString();
            log.info(exception);
        }
        clock.stop();  //计时结束

        final String finalException = exception;
        final Object finalResult = result;
        final Class clazz = methodInvocation.getThis().getClass();
        threadPool.submit(new Runnable() {
            @Override
            public void run() {
                trackPointService.persistenceRpcInvoke(
                        new TrackPointInfo().setParam(param)
                        .setClazz(clazz.getName())
                        .setResponse(JSONObject.toJSONString(finalResult))
                        .setException(finalException)
                        .setMethod(method)
                        .setRunTime(clock.getTime())
                );
            }
        });

        return result;
    }
}
