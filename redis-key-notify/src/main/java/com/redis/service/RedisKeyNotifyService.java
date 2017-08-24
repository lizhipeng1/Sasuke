package com.redis.service;

import com.redis.pubsub.PubSubListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


/**
 * Created by hzlizhipeng on 2017/8/8.
 */
@Component
public class RedisKeyNotifyService {

    private static final Logger log= LoggerFactory.getLogger(RedisKeyNotifyService.class);

    @Autowired
    private RedisService redisService;
    @Autowired
    private PubSubListener pubSubListener;


    @PostConstruct
    public void init(){
        /**
         * 取出 现在redis 中的 身份标识
         */
    }



    /**
     * 删除流程的实例ID对应的key
     */
    @Async
    public void delKey(String redisKey){
        redisService.remove( redisKey );
    }
    /**
     * 取消订阅 redis 的 "__key*__:*"  key过期的类型的消息通知
     */
    public void unsubscribeExpireKey(){
        pubSubListener.punsubscribe("__key*__:*");
    }
    /**
     * 重置 key 的过期时间
     */
    @Async
    public void resetRedisKeyExpireTime(String redisKey , Long expireTime){
        redisService.remove( redisKey);
        redisService.set(redisKey , redisKey , expireTime);
    }

    /**
     * 订阅 redis 的 "__key*__:*"  key过期的类型的消息通知
     */
    public void psubscribeExpireKey(){
        redisService.psubscribeExpireKey( pubSubListener , "__key*__:*");
    }

}
