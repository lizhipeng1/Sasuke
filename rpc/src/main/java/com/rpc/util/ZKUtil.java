package com.rpc.util;

import com.alibaba.fastjson.JSONObject;
import com.rpc.bean.model.BeanDefinitionInfo;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.data.Stat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

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
        Assert.notNull(node);
       String nodes[] =  node.split("/");
       String createNode="";
       for(String nodeStr : nodes) {
           if(nodeStr==null || nodeStr.length()==0){
               continue;
           }
           createNode+="/"+nodeStr;
           try {
               if(client.checkExists().forPath(createNode) == null){
                   client.create().forPath(createNode);
               }
           } catch (Exception e) {
               e.printStackTrace();
           }
       }
        try {
            client.setData().forPath(createNode, JSONObject.toJSONString(data).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String  getNodeData(String node){
        try {
            return JSONObject.toJSONString(JSONObject.parse( client.getData().forPath(node) ) );
        } catch (Exception e) {
            logger.info(e.toString());
            return new String();
        }
    }

    public  static  <T>  T  getNodeObjectData(String node , T t){
        try {
            return (T) JSONObject.parseObject(getNodeData(node) , t.getClass());
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
        createNodeWithData("/order" ,"ss");
    }
}
