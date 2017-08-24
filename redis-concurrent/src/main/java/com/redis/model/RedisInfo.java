package com.redis.model;

/**
 * Created by hzlizhipeng on 2017/8/15_
 */
public class RedisInfo {

    private String redis_host= "127.0.0.1";
    private int redis_port= 6379;
    private String redis_password="Netease101012#$";
    private int redis_maxIdle=3;
    private long redis_maxWait=1000;
    private boolean redis_testOnBorrow=true;
    private int redis_timeout=100000;

    public RedisInfo(String redis_host, int redis_port, String redis_password, int redis_maxIdle, long redis_maxWait, boolean redis_testOnBorrow, int redis_timeout) {
        this.redis_host = redis_host;
        this.redis_port = redis_port;
        this.redis_password = redis_password;
        this.redis_maxIdle = redis_maxIdle;
        this.redis_maxWait = redis_maxWait;
        this.redis_testOnBorrow = redis_testOnBorrow;
        this.redis_timeout = redis_timeout;
    }

    public String getRedis_host() {
        return redis_host;
    }

    public void setRedis_host(String redis_host) {
        this.redis_host = redis_host;
    }

    public int getRedis_port() {
        return redis_port;
    }

    public void setRedis_port(int redis_port) {
        this.redis_port = redis_port;
    }

    public String getRedis_password() {
        return redis_password;
    }

    public void setRedis_password(String redis_password) {
        this.redis_password = redis_password;
    }

    public int getRedis_maxIdle() {
        return redis_maxIdle;
    }

    public void setRedis_maxIdle(int redis_maxIdle) {
        this.redis_maxIdle = redis_maxIdle;
    }

    public long getRedis_maxWait() {
        return redis_maxWait;
    }

    public void setRedis_maxWait(long redis_maxWait) {
        this.redis_maxWait = redis_maxWait;
    }

    public boolean isRedis_testOnBorrow() {
        return redis_testOnBorrow;
    }

    public void setRedis_testOnBorrow(boolean redis_testOnBorrow) {
        this.redis_testOnBorrow = redis_testOnBorrow;
    }

    public int getRedis_timeout() {
        return redis_timeout;
    }

    public void setRedis_timeout(int redis_timeout) {
        this.redis_timeout = redis_timeout;
    }
}
