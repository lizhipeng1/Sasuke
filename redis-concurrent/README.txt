并发访问redis 服务

指定路由规则 实现一致性hash  实现并发访问

1. 动态添加spring redis bean
2. 一致性hash  分布redis Bean
3. 重写redisService  实现操作

/////////////////////////////////////////

动态 redis 的服务的监控

一致性hash  节点退出的问题 redis 数据迁移


/////////////////////////////////////////
redis 多个数据源 如何配置



///////////////////////////////////////
redis 单机  集群封装配置


#redis 单点配置 dev qa
#redis.address=10.246.84.34:6379,10.246.84.35:6379,10.246.84.41:6379
redis.address=127.0.0.1:6379
redis.port=6379
redis.password=ntes163
redis.maxIdle=20
redis.maxActive=50
redis.maxWait=10000
redis.testOnBorrow=true
redis.timeout=100000