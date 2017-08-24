package com.redis.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ZKUtils {

    private static final Logger logger= LoggerFactory.getLogger(ZKUtils.class);
    private static final String connectString = "192.168.146.128:4180,192.168.146.128:4181,192.168.146.128:4182";
    private static CuratorFramework client;

    public static CuratorFramework getClient() {
        return client;
    }

    static {
        client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .sessionTimeoutMs(50000)
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

    public static void createNodeWithData(String node  , Object data){
        try {
            if(exitNode(node)){
                client.setData().forPath(node , JSONObject.toJSONString(data).getBytes());
            }else {
                client.create().forPath(node , JSONObject.toJSONString(data).getBytes());
            }

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

    public static boolean exitNode( String node){
        try {
            Object object = client.checkExists().forPath(node);
            if(object == null){
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
        createNode("/testas");
        System.out.println( exitNode("/testaa") );
    }
}
