package com.rpc.monitor.service;


import com.rpc.monitor.model.ServiceInfo;

import java.util.List;

/**
 * Created by hzlizhipeng on 2017/10/25.
 */
public interface HeartbeatService {

    void checkDieService(List<ServiceInfo> serviceInfoList);

}
