package com.rpc.monitor.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.rpc.monitor.constant.Constant;
import com.rpc.monitor.dao.ServiceInfoDao;
import com.rpc.monitor.model.BeanDefinitionInfo;
import com.rpc.monitor.model.ServiceInfo;
import com.rpc.monitor.service.RpcInfoService;
import com.rpc.monitor.util.ZKUtil;
import com.rpc.util.BeanUtils;
import com.rpc.util.HessianUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.util.List;

/**
 * Created by hzlizhipeng on 2017/10/25.
 */
@Service
public class RpcInfoServiceImpl implements RpcInfoService {

    private  static final Logger logger= LoggerFactory.getLogger(RpcInfoServiceImpl.class);


    @Autowired
    private ServiceInfoDao serviceInfoDao;

    @Override
    public List<ServiceInfo> getAllZKServiceInfo() {
        return getServiceInfoByPath(Constant.rootNode);
    }

    @Override
    public List<ServiceInfo> getAllDBServiceInfo(ServiceInfo serviceInfo) {
        return serviceInfoDao.selectByCondition(serviceInfo);
    }

    @Override
    public List<ServiceInfo> getServiceInfoByPath(String path) {
        return  doGetServiceInfoFromZk(path,ServiceInfo.class) ;
    }


    @Override
    public boolean persistenceServiceInfo() {
        List<ServiceInfo> serviceInfoList = getAllZKServiceInfo();
        return  serviceInfoDao.batchInsert(serviceInfoList) >0;
    }

    @Override
    public List<ServiceInfo> invokeServiceInfo( List<ServiceInfo> serviceInfoList ){
//        if(CollectionUtils.isNotEmpty( serviceInfoList )){
//
//            return Lists.transform(serviceInfoList, new Function<ServiceInfo, ServiceInfo>() {
//                @Override
//                public ServiceInfo apply(ServiceInfo serviceInfo) {
//                    Object object = null;
//                    try {
//                        object = HessianUtil.factory.create(Class.forName(serviceInfo.getInterfaceClazz()), serviceInfo.getRequestUrl());
//                    } catch ( Exception e) {
//                        logger.info(e.toString());
//                    }
//                    return serviceInfo.setRpcObject(object);
//                }
//            });
//        }
        return null;
    }

    private <T> T nodeDataToModel(String data , Class<T> clazz ) {
        String beanDefinitionStr = JSONObject.parse(data).toString();
        return JSONObject.parseObject( beanDefinitionStr , clazz);
    }

    private <T> List<T> doGetServiceInfoFromZk(String path , Class<T> clazz) {
        List<T> serviceInfoList = Lists.newArrayList();
        List<String> zkNodeInfo = ZKUtil.getChildrenNode(path);
        if(CollectionUtils.isNotEmpty(zkNodeInfo)){
            // 循环 取下一级目录
            for(String childNode : zkNodeInfo){
                serviceInfoList.addAll(  doGetServiceInfoFromZk( path+"/"+childNode  , clazz));
            }
        }else {
            // 这一级是 service 数据层级
            serviceInfoList.add( nodeDataToModel( ZKUtil.getNodeData( path ) , clazz) );
        }
        return serviceInfoList;
    }





}
