package com.zero.account.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import redis.embedded.RedisServer;

import java.io.IOException;


@Configuration
public class LocalRedisConfig {
    private final RedisServer redisServer;

    public LocalRedisConfig(@Value("${spring.data.redis.port}") int port) throws Exception {
        this.redisServer = new RedisServer(port);
    }

    @PostConstruct
    public void startRedis() throws IOException {
        this.redisServer.start();
    }

    @PreDestroy
    public void stopRedis() throws IOException {
        this.redisServer.stop();
    }
}