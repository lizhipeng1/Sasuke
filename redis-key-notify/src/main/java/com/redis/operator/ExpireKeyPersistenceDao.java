package com.redis.operator;

/**
 * Created by hzlizhipeng on 2017/8/21.
 */
public interface ExpireKeyPersistenceDao {

    void persistenceKey(String key, String type);
}
