package com.redis.fengzhuang;

import redis.clients.jedis.JedisCommands;

import java.util.List;
import java.util.WeakHashMap;

public class JedisServiceCacheImpl implements JedisService {
    /*
       容错 当redis 服务挂了以后临时保存数据
    */
    private WeakHashMap weakHashMap = new WeakHashMap();

    @Override
    public void resetJedisCommonds(JedisCommands jedisCommands) {

    }

    @Override
    public String set(String key, Object value) {
        weakHashMap.put( key , value);
        return key;
    }

    @Override
    public String set(String key, Object value, Integer expireTime) {
        weakHashMap.put( key , value);
        return key;
    }

    @Override
    public void remove(String... keys) {
        for (String key : keys){
            weakHashMap.remove( key );
        }
    }

    @Override
    public void remove(String key) {
        weakHashMap.remove( key );
    }

    @Override
    public boolean exists(String key) {
        return false;
    }

    @Override
    public String get(String key) {
        return weakHashMap.get( key ) == null ? null : weakHashMap.get(key).toString();
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return (T) weakHashMap.get( key );
    }

    @Override
    public <T> List<T> getArray(String key, Class<T> clazz) {
        return (List<T>) weakHashMap.get(key);
    }

    @Override
    public Long setNX(String key, Object value) {
        return -1L;
    }

    @Override
    public String getSet(String key, Object value) {
        return null;
    }

    @Override
    public Long incr(String key) {
        return -1L;
    }

    @Override
    public Long decr(String key) {
        return -1L;
    }
}
