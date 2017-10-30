package com.rpc.monitor.service;


import com.rpc.monitor.model.ServiceInfo;

import javax.xml.ws.Service;
import java.net.MalformedURLException;
import java.util.List;

public interface RpcInfoService {

    List<ServiceInfo> getAllZKServiceInfo();

    List<ServiceInfo> getAllDBServiceInfo(ServiceInfo serviceInfo);

    List<ServiceInfo> getServiceInfoByPath(String path);

    boolean persistenceServiceInfo();

    List<ServiceInfo> invokeServiceInfo( List<ServiceInfo> serviceInfoList );
}
