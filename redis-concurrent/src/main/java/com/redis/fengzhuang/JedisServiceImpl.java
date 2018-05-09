package com.redis.fengzhuang;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.JedisCommands;

import java.util.List;


public class JedisServiceImpl implements JedisService{

    protected Logger logger = LoggerFactory.getLogger(JedisServiceImpl.class);

    private JedisCommands jedisCommands;
    private String keyPrefix;


    public JedisServiceImpl(JedisCommands jedisCommands , String keyPrefix) {
        if(jedisCommands==null){
            throw new RuntimeException(" jedisCommands is Null !!!");
        }
        this.jedisCommands = jedisCommands;
        this.keyPrefix = keyPrefix+"_";
    }

    @Override
    public void resetJedisCommonds(JedisCommands jedisCommands) {
        this.jedisCommands = jedisCommands;
        if(jedisCommands==null){
            throw new RuntimeException(" jedisCommands is Null !!!");
        }
    }

    public String set(final String key, Object value) {
        return jedisCommands.set(key ,serializationObject(value));
    }


    public String set(final String key, Object value, Integer expireTime) {
        String finalKey = key;
        String result  = jedisCommands.set( finalKey, serializationObject(value));
        jedisCommands.expire( finalKey , expireTime);
        return result;
    }

    public void remove(final String... keys) {
        for (String key : keys) {
            remove(key);
        }
    }

    public void remove(final String key) {
        if (exists(key)) {
            jedisCommands.del(key);
        }
    }
    public String get(final String key) {
        String jsonStr = jedisCommands.get(key);
        return (jsonStr != null && !"".equals(jsonStr)) ?  JSON.parse(jsonStr).toString()  : null;
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        throw  new RuntimeException("'sssssssssss");
//        String jsonStr = jedisCommands.get( key);
//        return JSONObject.parseObject(jsonStr , clazz);
    }

    @Override
    public <T> List<T> getArray(String key, Class<T> clazz) {
        String jsonStr = jedisCommands.get( key);
        return JSONObject.parseArray( jsonStr , clazz);
    }

    @Override
    public Long setNX(String key, Object value) {
      return  jedisCommands.setnx( key, serializationObject(value));
    }

    @Override
    public String getSet(String key, Object value) {
       return  jedisCommands.getSet( key, serializationObject(value));
    }

    @Override
    public Long incr(String key) {
        return jedisCommands.incr(  key );
    }

    @Override
    public Long decr(String key) {
        return jedisCommands.decr( key);
    }


    public boolean exists(final String key) {
        return jedisCommands.exists(keyPrefix+key);
    }

//    private String String key) {
//        return keyPrefix+key;
//    }

    private String serializationObject(Object value) {
        return  JSON.toJSONString(value , SerializerFeature.WriteNonStringValueAsString);
    }

}
