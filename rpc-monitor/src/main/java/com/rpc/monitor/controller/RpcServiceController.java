package com.rpc.monitor.controller;

import com.rpc.annotation.Invoker.ServiceInvokerResource;
import com.rpc.monitor.model.ServiceInfo;
import com.rpc.monitor.service.HeartbeatService;
import com.rpc.monitor.service.JarService;
import com.rpc.monitor.service.RpcInfoService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.MalformedURLException;
import java.util.List;


@Controller
@RequestMapping("rpc")
public class RpcServiceController implements ApplicationContextAware {

    @Autowired
    private RpcInfoService rpcInfoService;
    @Autowired
    private JarService jarService;

    private ApplicationContext applicationContext;
//    @ServiceInvokerResource
//    private ActivitiService activitiService;

    @ResponseBody
    @RequestMapping("refresh")
    public boolean allServiceInfo(){
        return rpcInfoService.persistenceServiceInfo();
    }

//    @ResponseBody
//    @RequestMapping("activitiService")
//    public void activitiService(){
//        activitiService.addComment(null , null);
//    }

    @ResponseBody
    @RequestMapping("invoke")
    public void invokeServiceInfo(){
        List<ServiceInfo>  serviceInfoList = rpcInfoService.getAllZKServiceInfo();
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
        List<ServiceInfo>  serviceInfoList = rpcInfoService.getAllDBServiceInfo(null);
        heartbeatService.checkDieService( serviceInfoList );
//        System.out.println(JSONObject.toJSONString(serviceInfoList));

    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
