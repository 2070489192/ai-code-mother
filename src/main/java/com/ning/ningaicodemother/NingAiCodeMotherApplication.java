package com.ning.ningaicodemother;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.ning.ningaicodemother.mapper")
public class NingAiCodeMotherApplication {

    public static void main(String[] args) {
        SpringApplication.run(NingAiCodeMotherApplication.class, args);
    }

}
