package com.rpc.config;

/**
 * 配置文件的信息
 */
public class Config {

    private String rpcServerPrefix ;

    public Config(String rpcServerPrefix) {
        this.rpcServerPrefix = rpcServerPrefix;
    }

    public String getRpcServerPrefix() {
        return rpcServerPrefix;
    }
}
