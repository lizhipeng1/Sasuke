package com.rpc.monitor.service;


import com.rpc.monitor.model.ServiceInfo;

import java.net.MalformedURLException;
import java.util.List;

public interface RpcInfoService {

    List<ServiceInfo> getAllServiceInfo();

    List<ServiceInfo> getServiceInfoByPath(String path);

    boolean persistenceServiceInfo();

    List<Object> invokeServiceInfo( List<ServiceInfo> serviceInfoList );
}
