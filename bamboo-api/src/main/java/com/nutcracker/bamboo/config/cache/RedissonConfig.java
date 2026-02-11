package com.nutcracker.bamboo.config.cache;

import java.io.IOException;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Redisson Config
 *
 * @author 胡桃夹子
 * @since 2023-02-09 21:13
 */
@Slf4j
@Configuration
public class RedissonConfig {

    @SuppressWarnings("null")
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient(@Value("${spring.redis.redisson.file}") String path) throws IOException {
        log.debug("redissonConfig={}", path);
        String config = StrUtil.replace(path, "classpath:", "");
        log.info("Redisson path={}", config);
        return Redisson.create(Config.fromYAML(new ClassPathResource(config).getInputStream()));
    }
}
