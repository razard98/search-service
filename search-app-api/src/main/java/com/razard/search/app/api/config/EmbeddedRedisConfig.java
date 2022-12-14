package com.razard.search.app.api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.Objects;

@Profile("local")
@Slf4j
@Configuration
public class EmbeddedRedisConfig {

    @Value("${spring.redis.port}")
    private int redisPort;

    private RedisServer redisServer;

    @PostConstruct
    public void redisServer() {
        log.debug("EmbeddedRedis Started!!");
        redisServer = RedisServer.builder().port(redisPort).setting("maxmemory 128M").build();
        redisServer.start();
    }

    @PreDestroy
    public void stopRedis() {
        if (Objects.nonNull(redisServer) && redisServer.isActive()) {
            redisServer.stop();
        }
    }
}