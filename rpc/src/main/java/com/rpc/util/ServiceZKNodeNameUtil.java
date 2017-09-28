package com.rpc.util;


public class ServiceZKNodeNameUtil {

    public static String getServiceZKNodeName(String environment , String classFullName ){
        String nodeName = "/"+environment;
        nodeName+="/"+classFullName;
        return nodeName;
    }

}
