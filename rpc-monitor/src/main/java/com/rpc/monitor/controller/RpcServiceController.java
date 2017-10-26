package com.rpc.monitor.controller;

import com.alibaba.fastjson.JSONObject;
import com.rpc.monitor.model.ServiceInfo;
import com.rpc.monitor.service.HeartbeatService;
import com.rpc.monitor.service.JarService;
import com.rpc.monitor.service.RpcInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.util.List;


@Controller
@RequestMapping("rpc")
public class RpcServiceController {

    @Autowired
    private RpcInfoService rpcInfoService;
    @Autowired
    private JarService jarService;

    @ResponseBody
    @RequestMapping("refresh")
    public boolean allServiceInfo(){
        return rpcInfoService.persistenceServiceInfo();
    }

    @ResponseBody
    @RequestMapping("invoke")
    public void invokeServiceInfo(){
        List<ServiceInfo>  serviceInfoList = rpcInfoService.getAllServiceInfo();
        rpcInfoService.invokeServiceInfo(serviceInfoList);
    }

    @ResponseBody
    @RequestMapping("jar")
    public void loadjar(String filePath){
        try {
            jarService.loadJarClass(filePath);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Autowired
    private HeartbeatService heartbeatService;

    @ResponseBody
    @RequestMapping("beat")
    public void  die(String filePath){
        List<ServiceInfo>  serviceInfoList = rpcInfoService.getAllServiceInfo();
        heartbeatService.checkDieService( serviceInfoList );
//        System.out.println(JSONObject.toJSONString(serviceInfoList));

    }


}
