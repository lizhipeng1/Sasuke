package com.rpc.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;

public class ZKUtil {

    private static final Logger logger= LoggerFactory.getLogger(ZKUtil.class);

    private  String connectString ;

    private Integer baseSleepTimeMs;

    private Integer maxRetries;

    public ZKUtil(String connectString, Integer baseSleepTimeMs, Integer maxRetries) {
        this.connectString = connectString;
        this.baseSleepTimeMs = baseSleepTimeMs;
        this.maxRetries = maxRetries;
    }

    private   CuratorFramework client;



    public void init(){
        client = CuratorFrameworkFactory.builder()
                .connectString(connectString)
                .retryPolicy(new ExponentialBackoffRetry(baseSleepTimeMs, maxRetries))
                .build();
        client.start();
    }

    public   void deleteNode( String node ){
        try {
            client.delete().forPath(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public   void createNode( String node ){
        try {
            client.create().forPath(node);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public   void addNodeData(String node , Object data){
        try {
            client.setData().forPath(node , JSONObject.toJSONString(data).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public   void createNodeWithData(String node  , Object data){
        Assert.notNull(node);
        String createNode = createNodeAllNodePath(node);
        try {
            client.setData().forPath(createNode, JSONObject.toJSONString(data).getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public   String createNodeAllNodePath(String node ){
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
        return  createNode;
    }


    public   String  getNodeData(String node){
        try {
            return JSONObject.toJSONString(JSONObject.parse( client.getData().forPath(node) ) );
        } catch (Exception e) {
            logger.info(e.toString());
            return new String();
        }
    }

    public     <T>  T  getNodeObjectData(String node , T t){
        try {
            return (T) JSONObject.parseObject(getNodeData(node) , t.getClass());
        } catch (Exception e) {
            logger.info(e.toString());
            return null;
        }
    }


    public   boolean exitNode( String node){
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
//        createNodeWithData("/order" ,"ss");
    }
}
