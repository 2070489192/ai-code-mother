package com.ning.ningaicodemother.config;

import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "spring.data.redis")
@Data
public class RedisChatMemoryStoreConfig {

    private String host;

    private int port;

    private String password;

    private String username;

    private long ttl;

    @Bean
    public RedisChatMemoryStore redisChatMemoryStore() {
        System.out.println("====== 实际注入的值 ======");
        System.out.println("host=[" + host + "]");
        System.out.println("port=[" + port + "]");
        System.out.println("username=[" + username + "]");
        System.out.println("password=[" + password + "]");
        System.out.println("ttl=[" + ttl + "]");
        System.out.println("==========================");

        return RedisChatMemoryStore.builder()
                .user(username)
                .host(host)
                .port(port)
                .password(password)
                .ttl(ttl)
                .build();
    }
}