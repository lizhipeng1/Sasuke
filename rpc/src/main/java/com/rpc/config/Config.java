package com.rpc.config;

/**
 * 配置文件的信息
 */
public class Config {

    private String rpcServerPrefix ;
    private String projectName;
    private Integer version;

    public Config(String rpcServerPrefix, String projectName, Integer version) {
        this.rpcServerPrefix = rpcServerPrefix;
        this.projectName = projectName;
        this.version = version;
    }

    public String getRpcServerPrefix() {
        return rpcServerPrefix;
    }

    public String getProjectName() {
        return projectName;
    }

    public Integer getVersion() {
        return version;
    }
}
