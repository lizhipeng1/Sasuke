package com.rpc.annotation.config;

/**
 * 服务环境的配置信息
 */
public class ServiceProfileConfig {
    private boolean startRpc = true;    //是否开启Rpc扫描


    public boolean isStartRpc() {
        return startRpc;
    }

    public void setStartRpc(boolean startRpc) {
        this.startRpc = startRpc;
    }
}
