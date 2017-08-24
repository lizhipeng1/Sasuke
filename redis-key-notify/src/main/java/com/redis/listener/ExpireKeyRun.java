package com.redis.listener;


import com.redis.ThreadExecutor;
import com.redis.service.RedisKeyNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

@Component
public class ExpireKeyRun implements ApplicationContextAware  , Runnable {
    private static final Logger log= LoggerFactory.getLogger(ExpireKeyRun.class);

    private ApplicationContext applicationContext;

    @Resource
    private RedisKeyNotifyService redisKeyNotifyService;
    @Autowired
    private ThreadExecutor threadExecutor;

    @PostConstruct
    public void init(){
        ExpireKeyRun expireKeyRun = this.applicationContext.getBean(ExpireKeyRun.class);
        threadExecutor.execute( expireKeyRun );
    }

    public void psubscribe(){
        /**
         * 生成秘钥 上传zk 用作区分 留作消息通知的判断是不是要业务处理
         */



        log.info("开启订阅 redis 的key 过期消息");
        redisKeyNotifyService.psubscribeExpireKey();
    }

    public void unsubscribe(){
        log.info("取消订阅 redis 的key 过期消息");
        redisKeyNotifyService.unsubscribeExpireKey();
    }

    public void closeSubscribe(){
        log.info("发出消息 到注册中心 通知现在订阅关闭");
        redisKeyNotifyService.unsubscribeExpireKey();
    }



    public void run() {
        psubscribe();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
