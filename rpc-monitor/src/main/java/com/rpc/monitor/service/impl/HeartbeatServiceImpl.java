package com.rpc.monitor.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.rpc.monitor.model.ServiceInfo;
import com.rpc.monitor.service.HeartbeatService;
import com.rpc.monitor.service.RpcInfoService;
import com.rpc.util.ReflectionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by hzlizhipeng on 2017/10/25.
 */
@Service
public class HeartbeatServiceImpl implements HeartbeatService {
    private  static final Logger logger= LoggerFactory.getLogger(HeartbeatServiceImpl.class);

    private static final String heartbeatMethodName ="heartbeat";

    @Autowired
    private RpcInfoService rpcInfoService;

    @Override
    public void checkDieService(List<ServiceInfo> serviceInfoList) {
        // 1. 心跳检测
        // 2. 跟新数据库为记录 死亡
        List<Object> objectList = rpcInfoService.invokeServiceInfo( serviceInfoList );
        if(CollectionUtils.isNotEmpty(objectList)){
            List<String>  dieClassString = doHeartCheckHessianService( objectList );
            System.out.println(JSONObject.toJSONString( dieClassString ));
        }
    }

    private List<String> doHeartCheckHessianService(List<Object> objectList) {
        List<String> clazzNames = Lists.newArrayList();
        //  做 对象服务 匹配
        for(Object object : objectList){
            Class clazz = ReflectionUtils.getTargetClass( object );
            Boolean result = (Boolean) ReflectionUtils.invokeMethod( object ,  heartbeatMethodName ,null , null);
        }
        return clazzNames;
    }
}
