package com.nutcracker.bamboo.common.util;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.redisson.api.RBucket;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

/**
 * Redisson 工具类
 * 用于替代 Spring Data Redis 的 RedisTemplate
 * 
 * @author 胡桃夹子
 */
@Component
@RequiredArgsConstructor
public class RedissonUtil {

    private final RedissonClient redissonClient;

    /**
     * 设置值
     */
    public void set(String key, Object value) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value);
    }

    /**
     * 设置值并指定过期时间
     */
    public void set(String key, Object value, long ttl, TimeUnit timeUnit) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        bucket.set(value, java.time.Duration.ofMillis(timeUnit.toMillis(ttl)));
    }

    /**
     * 获取值
     */
    public Object get(String key) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.get();
    }

    /**
     * 删除键
     */
    public boolean delete(String key) {
        return redissonClient.getBucket(key).delete();
    }

    /**
     * 检查键是否存在
     */
    public boolean hasKey(String key) {
        return redissonClient.getBucket(key).isExists();
    }

    /**
     * Hash 操作 - 设置字段值
     */
    public void hashPut(String key, String field, Object value) {
        RMap<String, Object> map = redissonClient.getMap(key);
        map.put(field, value);
    }

    /**
     * Hash 操作 - 批量设置字段值
     */
    public void hashPutAll(String key, Map<? extends String, ? extends Object> map) {
        RMap<String, Object> rMap = redissonClient.getMap(key);
        rMap.putAll(map);
    }

    /**
     * Hash 操作 - 获取字段值
     */
    public Object hashGet(String key, Object field) {
        RMap<String, Object> map = redissonClient.getMap(key);
        return map.get(field);
    }

    /**
     * Hash 操作 - 批量获取字段值
     */
    public List<Object> hashMultiGet(String key, Collection<Object> fields) {
        RMap<String, Object> map = redissonClient.getMap(key);
        java.util.Set<String> stringFields = new java.util.HashSet<>();
        for (Object field : fields) {
            if (field instanceof String) {
                stringFields.add((String) field);
            }
        }
        return new java.util.ArrayList<>(map.getAll(stringFields).values());
    }

    /**
     * Hash 操作 - 删除字段
     */
    public boolean hashDelete(String key, Object field) {
        RMap<String, Object> map = redissonClient.getMap(key);
        return map.remove(field) != null;
    }

    /**
     * 自增操作
     */
    public Long increment(String key) {
        RBucket<Long> bucket = redissonClient.getBucket(key);
        Long currentValue = bucket.get();
        if (currentValue == null) {
            currentValue = 0L;
        }
        bucket.set(currentValue + 1);
        return currentValue + 1;
    }

    /**
     * 设置过期时间
     */
    public boolean expire(String key, long timeout, TimeUnit timeUnit) {
        RBucket<Object> bucket = redissonClient.getBucket(key);
        return bucket.expire(java.time.Duration.ofMillis(timeUnit.toMillis(timeout)));
    }
}
