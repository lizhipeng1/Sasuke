package com.rpc.util;

import com.alibaba.fastjson.JSONObject;
import com.rpc.bean.model.BeanDefinitionInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


public class ZKUtil {

    private static final Logger logger= LoggerFactory.getLogger(ZKUtil.class);
    private static final String connectString = "192.168.146.128:4180,192.168.146.128:4181,192.168.146.128:4182";
    private static CuratorFramework client;

    public static CuratorFramework getClient() {
        return client;
    }

    static {
        client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
    }

    public static void createNode( String node ){
        try {
            client.create().forPath(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addNodeData(String node , Object data){
        try {
            client.setData().forPath(node , JSONObject.toJSONString(data).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createNodeWithData(String node  , Object data){
        try {
            client.create().forPath(node , JSONObject.toJSONString(data).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String  getNodeData(String node){
        try {
           return String.valueOf(client.getData().forPath(node));
        } catch (Exception e) {
            logger.info(e.toString());
            return new String();
        }
    }

    public  static  <T>  T  getNodeObjectData(String node , T t){
        try {
            return (T) JSONObject.parseObject(String.valueOf(client.getData().forPath(node)) , t.getClass());
        } catch (Exception e) {
            logger.info(e.toString());
            return null;
        }
    }


    public static boolean exitNode( String node){
        try {
            if(client.checkExists().forPath(node) == null){
                return false;
            }else {
                return true;
            }
        } catch (Exception e) {
            logger.info(e.toString());
            return false;
        }
    }


    public static void main(String[] args) {
//        createNodeWithData("/dev-com-netease-corp-it-order-common-service-RemoteOrderService" ,"ss");
//        System.out.println( getNodeData("/dev-com-netease-corp-it-order-common-service-RemoteOrderService") );

        byte[] bs = JSONObject.toJSONBytes( "this is a test");

        System.out.println( JSONObject.parse( bs ));


    }
}
