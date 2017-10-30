package com.rpc.monitor.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.rpc.monitor.dao.ServiceInfoDao;
import com.rpc.monitor.model.ServiceInfo;
import com.rpc.monitor.service.HeartbeatService;
import com.rpc.monitor.service.RpcInfoService;
import com.rpc.monitor.util.HttpUtil;
import com.rpc.util.ReflectionUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.*;

/**
 * Created by hzlizhipeng on 2017/10/25.
 */
@Service
public class HeartbeatServiceImpl implements HeartbeatService {
    private  static final Logger logger= LoggerFactory.getLogger(HeartbeatServiceImpl.class);

    private ExecutorService threadPool = Executors.newFixedThreadPool(1);

    @Autowired
    private ServiceInfoDao serviceInfoDao;
    @Autowired
    private RpcInfoService rpcInfoService;

    @Override
    public void checkDieService(final List<ServiceInfo> serviceInfoList) {
        // 1. 心跳检测
        // 2. 跟新数据库为记录 死亡
//        List<ServiceInfo> objectList = rpcInfoService.invokeServiceInfo( serviceInfoList );
        if(CollectionUtils.isNotEmpty(serviceInfoList)){
            Future future = threadPool.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    return doHeartCheckHessianService( serviceInfoList );
                }
            });
            List<ServiceInfo>  serviceInfosUpdateDB = null;
            try {
                serviceInfosUpdateDB = (List<ServiceInfo>) future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            if(CollectionUtils.isNotEmpty( serviceInfosUpdateDB )) {
                for (ServiceInfo serviceInfo : serviceInfosUpdateDB){
                    serviceInfo.setAlive(0);
                    serviceInfoDao.updateByPrimaryKey(serviceInfo);
                }
            }
            System.out.println(JSONObject.toJSONString( serviceInfosUpdateDB ));
        }
    }

    private  List<ServiceInfo> doHeartCheckHessianService( List<ServiceInfo> serviceInfos) {
        List<ServiceInfo> backServiceInfos = Lists.newArrayList();
        //  做 对象服务 匹配
        for(ServiceInfo serviceInfo : serviceInfos){
            String result = null;
            try {
                result = HttpUtil.doGet(serviceInfo.getRequestUrl());
            }catch (Exception e){
                logger.info(e.toString());
            }

            if(!doJudgeResultIsDie(result)){
                backServiceInfos.add( serviceInfo );
            }
        }
        return backServiceInfos;
    }

    private boolean doJudgeResultIsDie(String result) {
        if(StringUtils.isNotBlank(result)){
            JSONObject jsonObject = JSONObject.parseObject( result );
            if(jsonObject.getInteger("status").intValue() == 405 &&
                    jsonObject.getString("error").equals("Method Not Allowed") &&
                    jsonObject.getString("message").equals("HessianServiceExporter only supports POST requests")){
                return true;
            }
        }
        return false;
    }
}
