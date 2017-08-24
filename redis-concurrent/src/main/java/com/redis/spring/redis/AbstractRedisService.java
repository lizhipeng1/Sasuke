package com.redis.spring.redis;

import com.redis.model.RedisInfo;
import com.redis.shard.Shard;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;

public abstract  class AbstractRedisService  implements BeanFactoryPostProcessor , ApplicationContextAware,ApplicationListener {

    private Shard shard;
    private ApplicationContext applicationContext;

    private List<RedisInfo> redisInfos = new ArrayList<RedisInfo>(){{
        add(new RedisInfo("127.0.0.1",6379 ,"Netease101012#$",3,1000,true,10000));
        add(new RedisInfo("127.0.0.1",6380 ,"Netease101012#$",3,1000,true,10000));
        add(new RedisInfo("127.0.0.1",6381 ,"Netease101012#$",3,1000,true,10000));
    }};

    public RedisTemplate getRedisTemplate( String key ) {
        return shard.getShardInfo(key);
    }


    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        List<RedisTemplate> templateList =  getRedisTemplateList();
        shard = new Shard( templateList );
    }
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public void onApplicationEvent(ApplicationEvent applicationEvent) {

    }
    /**
     * 创建 redisTemplate  实例集合
     * @return
     */
    private List<RedisTemplate> getRedisTemplateList() {

        DefaultListableBeanFactory defaultListableBeanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();

        List<RedisTemplate> templateList = new ArrayList<RedisTemplate>();

        for(RedisInfo redisInfo : redisInfos){
            BeanDefinitionBuilder jedisPoolConfigDefinition = BeanDefinitionBuilder.rootBeanDefinition(JedisPoolConfig.class);
            jedisPoolConfigDefinition.addPropertyValue("maxIdle" , redisInfo.getRedis_maxIdle());
            jedisPoolConfigDefinition.addPropertyValue("maxWaitMillis" , redisInfo.getRedis_maxWait());
            jedisPoolConfigDefinition.addPropertyValue("testOnBorrow" , true);

            defaultListableBeanFactory.registerBeanDefinition(getJedisPoolConfigName(redisInfo), jedisPoolConfigDefinition.getBeanDefinition());


            BeanDefinitionBuilder jedisConnectionFactoryDefinition = BeanDefinitionBuilder.rootBeanDefinition(JedisConnectionFactory.class);
            jedisConnectionFactoryDefinition.addPropertyReference("poolConfig" , getJedisPoolConfigName(redisInfo) );
            jedisConnectionFactoryDefinition.addPropertyValue("port" , redisInfo.getRedis_port());
            jedisConnectionFactoryDefinition.addPropertyValue("hostName" , redisInfo.getRedis_host());
            jedisConnectionFactoryDefinition.addPropertyValue("password" , redisInfo.getRedis_password());
            jedisConnectionFactoryDefinition.addPropertyValue("timeout" , redisInfo.getRedis_timeout());

            defaultListableBeanFactory.registerBeanDefinition(getJedisConnectionFactoryName(redisInfo), jedisConnectionFactoryDefinition.getBeanDefinition());

            BeanDefinitionBuilder redisTemplateyDefinition = BeanDefinitionBuilder.rootBeanDefinition(RedisTemplate.class);
            redisTemplateyDefinition.addPropertyReference("connectionFactory" , getJedisConnectionFactoryName(redisInfo) );
            redisTemplateyDefinition.addPropertyValue("keySerializer" ,new StringRedisSerializer());
            redisTemplateyDefinition.addPropertyValue("valueSerializer" ,  new StringRedisSerializer());

            defaultListableBeanFactory.registerBeanDefinition(getRedisTemplateName(redisInfo), redisTemplateyDefinition.getBeanDefinition());

            templateList.add((RedisTemplate) applicationContext.getBean(getRedisTemplateName(redisInfo)));

        }

        return templateList;
    }

    private String getRedisTemplateName(RedisInfo redisInfo) {
        return "redisTemplate"+redisInfo.getRedis_host()+"/"+redisInfo.getRedis_port();
    }

    private String getJedisPoolConfigName(RedisInfo redisInfo) {
        return "jedisPool"+redisInfo.getRedis_host()+"/"+redisInfo.getRedis_port();
    }
    private String getJedisConnectionFactoryName(RedisInfo redisInfo) {
        return "jedisConnection"+redisInfo.getRedis_host()+"/"+redisInfo.getRedis_port();
    }

}
