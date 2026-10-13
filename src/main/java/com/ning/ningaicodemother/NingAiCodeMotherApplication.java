package com.ning.ningaicodemother;

import dev.langchain4j.community.store.embedding.redis.spring.RedisEmbeddingStoreAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(exclude = {RedisEmbeddingStoreAutoConfiguration.class})
@MapperScan("com.ning.ningaicodemother.mapper")
public class NingAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(NingAiCodeMotherApplication.class, args);
    }

}
