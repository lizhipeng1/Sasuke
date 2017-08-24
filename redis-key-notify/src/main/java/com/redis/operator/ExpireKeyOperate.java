package com.redis.operator;


/**
 * key 过期的后的 事件操作调用方实现
 */
public interface ExpireKeyOperate {
    void doTimeOutKeyOperate(String pattern, String channel, String message);
}
