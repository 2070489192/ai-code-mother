package com.ning.ningaicodemother.config;

import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
// 关键：全部使用 tools.jackson 包
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.module.SimpleModule;
import tools.jackson.databind.ser.std.ToStringSerializer;

@Configuration
public class JacksonLongConfig {

    @Bean
    public JsonMapperBuilderCustomizer longToStringCustomizer() {
        return b -> {
            // 这里现在用的是 tools.jackson.databind.module.SimpleModule
            SimpleModule module = new SimpleModule();
            module.addSerializer(Long.class, ToStringSerializer.instance);
            module.addSerializer(Long.TYPE, ToStringSerializer.instance);
            b.addModule(module);
        };
    }
}