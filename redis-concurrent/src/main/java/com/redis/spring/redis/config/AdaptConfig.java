package com.redis.spring.redis.config;

import com.redis.model.RedisInfo;

import java.util.List;

/**
 * Created by hzlizhipeng on 2017/8/21.
 */
public interface AdaptConfig {
      void addRedisInfoList(List<RedisInfo> redisInfos);
}
