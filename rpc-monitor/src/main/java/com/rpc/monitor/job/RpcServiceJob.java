package com.rpc.monitor.job;

import com.rpc.monitor.service.RpcInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RpcServiceJob {
    private static final Logger log = LoggerFactory.getLogger(RpcServiceJob.class);

    @Autowired
    private RpcInfoService rpcInfoService;

    /**
     * 1 小时执行一次 服务数据持久化任务
     */
    @Scheduled(cron = "0 0 0/1  * * ?")
    public void doEhrJob() {
        log.info("====== 开始持久化服务数据到数据库 ======");
        boolean result = rpcInfoService.persistenceServiceInfo();
        log.info("============ 持久化结束 结果："+result+"============");
    }



}
