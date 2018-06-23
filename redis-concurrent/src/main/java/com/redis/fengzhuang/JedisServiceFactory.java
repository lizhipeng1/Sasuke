package com.redis.fengzhuang;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import redis.clients.jedis.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashSet;
import java.util.Set;

public class JedisServiceFactory implements FactoryBean<Object>, InitializingBean, ApplicationContextAware {
    protected Logger logger = LoggerFactory.getLogger(JedisServiceFactory.class);

    private String environment;
    private String keyPrefix = "@redis-";
    private String redisAddr;
    private String redisPassword;
    private Integer redisTimeout;
    private Integer redisMaxRedirections;
    private GenericObjectPoolConfig genericObjectPoolConfig;

    private JedisService jedisService;
    private JedisPool jedisPool;

    private ApplicationContext applicationContext;

    private JedisServiceCacheImpl jedisServiceCache = new JedisServiceCacheImpl();

    private ThreadLocal<JedisCommands> threadLocal = new ThreadLocal();

    @Override
    public Object getObject() {
        return createProxy();
    }

    private Object createProxy() {
        return Proxy.newProxyInstance(jedisService.getClass().getClassLoader(), jedisService.getClass().getInterfaces(), (proxy, method, args) -> {
            args[0] = businessKey((String) args[0]);
            Object  result = null;
            try {
                if(jedisPool !=null && threadLocal.get() == null) {
                    threadLocal.set(jedisPool.getResource());
                    // TODO: 会有线程安全问题
                    jedisService.resetJedisCommonds(threadLocal.get());
                }
                result = method.invoke(jedisService, args);
            }catch (Exception e){
                logger.info("Exception e : " +e.toString());
                result = method.invoke(jedisServiceCache, args);
            }finally {
                refreshJedisResource();
            }
            return result;
        });
    }

    private void refreshJedisResource() {
        if(threadLocal.get() instanceof Jedis){
            Jedis jedis = (Jedis) threadLocal.get();
            jedis.close();
            threadLocal.remove();   // 置空 下次重新获取
        }
    }

    @Override
    public Class<?> getObjectType() {
        return JedisService.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private Set<HostAndPort> parseClusterHostAndPort() {
        Set<HostAndPort> haps = new HashSet<>();
        // , split 拆分每一个
        String[] addresses  = this.redisAddr.split(",");
        for(String addr : addresses ){
            String[] ipAndPort = addr.split(":");
            HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
            haps.add(hap);
        }
        return haps;
    }

    @Override
    public void afterPropertiesSet() {
        JedisCommands jedisCommands;
        if(!applicationContext.containsBean("poolConfig")){
            throw new RuntimeException(" not found poolConfig in spring !!");
        }
        if( environment.startsWith("dev") || environment.startsWith("qa")){    // dev qa
            getJedisPool();
            threadLocal.set(jedisPool.getResource());
            jedisCommands = threadLocal.get();
        }else if(environment.startsWith("pro")){ // pro
            Set<HostAndPort> haps = this.parseClusterHostAndPort();
            jedisCommands = new JedisCluster(haps, redisTimeout, redisTimeout , redisMaxRedirections , redisPassword , genericObjectPoolConfig);
        }else {
            throw new RuntimeException("can not  get service environment info !!!!");
        }
        jedisService =  new JedisServiceImpl(jedisCommands , keyPrefix);
    }

    private JedisPool getJedisPool() {
        if(jedisPool!=null) {
            jedisPool.destroy();
        }
        HostAndPort hostAndPort =  this.parseRedisHostAndPort();
        jedisPool = new JedisPool( genericObjectPoolConfig , hostAndPort.getHost() , hostAndPort.getPort() , redisTimeout , redisPassword );
        return jedisPool;
    }

    private HostAndPort parseRedisHostAndPort() {
        String[] addresses  = this.redisAddr.split(",");
        if(ArrayUtils.isEmpty(addresses)){
            throw new RuntimeException(" please confirm redis host and port！！");
        }else {
            for (String addr : addresses) {
                String[] ipAndPort = addr.split(":");
                HostAndPort hap = new HostAndPort(ipAndPort[0], Integer.parseInt(ipAndPort[1]));
                return hap;
            }
        }
        return null;
    }

    private String businessKey(String key) {
        return keyPrefix+key;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void setRedisAddr(String redisAddr) {
        this.redisAddr = redisAddr;
    }

    public void setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
    }

    public void setRedisTimeout(Integer redisTimeout) {
        this.redisTimeout = redisTimeout;
    }

    public void setRedisMaxRedirections(Integer redisMaxRedirections) {
        this.redisMaxRedirections = redisMaxRedirections;
    }

    public void setJedisService(JedisService jedisService) {
        this.jedisService = jedisService;
    }

    public void setGenericObjectPoolConfig(GenericObjectPoolConfig genericObjectPoolConfig) {
        this.genericObjectPoolConfig = genericObjectPoolConfig;
    }
    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public void setKeyPrefix(String keyPrefix) {
        this.keyPrefix = keyPrefix+"_";
    }
}
