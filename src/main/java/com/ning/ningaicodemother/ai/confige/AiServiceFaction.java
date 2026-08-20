package com.ning.ningaicodemother.ai.confige;


import com.ning.ningaicodemother.ai.service.AiService;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Ai 服务生成工厂,用于创建 Ai 服务实例.
@Configuration
public class AiServiceFaction {

    @Resource
    private ChatModel chatModel;

    @Bean
    public AiService aiCodeGenerate(){
        // 使用 langchain4j 用于创建 Ai 服务实例的方法
       return AiServices.create(AiService.class,chatModel);
    }
}
