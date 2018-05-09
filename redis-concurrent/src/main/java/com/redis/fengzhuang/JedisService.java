package com.redis.fengzhuang;


import redis.clients.jedis.JedisCommands;

import java.util.List;

public interface JedisService {


    /**
     * 重置jedisCommonds
     * @param jedisCommands
     */
    void resetJedisCommonds(JedisCommands jedisCommands);

    /**
     * 写入缓存
     * @param key
     * @param value
     * @return
     */
    String set(final String key, Object value);

     /**
      * 写入缓存设置时效时间 秒
      * @param key
      * @param value
      * @return
      */
     String set(final String key, Object value, Integer expireTime);

    /**
     * 批量删除对应的value
     * @param keys
     */
    void remove(final String... keys);

    /**
     * 删除 指定的 一个key
     * @param key
     */
    void remove(final String key);

    /**
     * 是否存在一个key
     * @param key
     * @return
     */
    boolean exists(final String key);

    /**
     * 读取缓存
     * @param key
     * @return
     */
    String get(final String key);

    /**
     * 读取缓存
     * @param key
     * @return
     */
    <T> T get(final String key, Class<T> clazz);

    /**
     * 读取缓存
     * @param key
     * @return
     */
    <T> List<T> getArray(final String key, Class<T> clazz);

    /**
     * redis setNX
     * @param key
     * @param value
     */
    Long setNX(final String key, Object value);

    /**
     *  getSet
     * @param key
     * @param value
     * @return
     */
    String getSet(final String key, Object value);

    /**
     * redis 自增
     * @param key
     */
    Long incr(final String key);

    /**
     * redis 自减
     * @param key
     * @return
     */
    Long decr(final String key);
}
