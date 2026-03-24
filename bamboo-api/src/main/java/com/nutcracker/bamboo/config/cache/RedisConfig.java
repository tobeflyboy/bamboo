package com.nutcracker.bamboo.config.cache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.redisson.api.RedissonClient;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.cache.interceptor.CacheResolver;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.cache.interceptor.SimpleCacheErrorHandler;
import org.springframework.cache.interceptor.SimpleCacheResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import com.nutcracker.bamboo.common.constant.CacheableKey;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Redis 配置（基于 Redisson）
 *
 * @author 胡桃夹子
 * @date 2020-03-01 09:49
 */
@Slf4j
@RequiredArgsConstructor
@Configuration
public class RedisConfig implements CachingConfigurer {

    private final RedissonClient redissonClient;

    private final Environment environment;

    @EventListener(ApplicationReadyEvent.class)
    public void printRedisConfig() {
        String configPath = environment.getProperty("spring.redis.redisson.file");
        log.warn("🔥 REDIS CONFIG | Using Redisson with config: {}", configPath);
    }

    /**
     * 重写 KeyGenerator
     * 自定义 redis-key
     *
     * @return KeyGenerator
     */
    @Override
    @Bean
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            String keyGenerator = method.getName() + ":" + StrUtil.join(":", params);
            log.info("keyGenerator={}", keyGenerator);
            return keyGenerator;
        };
    }

    @Bean
    @Override
    public CacheResolver cacheResolver() {
        return new SimpleCacheResolver(Objects.requireNonNull(cacheManager()));
    }

    @Bean
    @Override
    public CacheErrorHandler errorHandler() {
        // 用于捕获从 Cache 中进行 CRUD 时的异常的回调处理器。
        return new SimpleCacheErrorHandler();
    }

    @Bean
    @Override
    public CacheManager cacheManager() {
        Map<String, CacheConfig> configMap = new HashMap<>();
        Map<String, Long> keyMap = CacheableKey.getCacheableKeyMap();
        for (Map.Entry<String, Long> entry : keyMap.entrySet()) {
            if (log.isDebugEnabled()) {
                log.debug("CacheManager cacheNames=[{}],timeout=[{}]s", entry.getKey(), entry.getValue());
            }
            // TTL 和 MaxIdleTime 都设置为配置的过期时间（秒）
            CacheConfig config = new CacheConfig(entry.getValue() * 1000, entry.getValue() * 1000);
            configMap.put(entry.getKey(), config);
        }
        return new RedissonSpringCacheManager(redissonClient, configMap);
    }
}
