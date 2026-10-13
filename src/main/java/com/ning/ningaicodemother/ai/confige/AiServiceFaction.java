package com.ning.ningaicodemother.ai.confige;


import com.ning.ningaicodemother.ai.service.AiService;
import com.ning.ningaicodemother.service.ChatHistoryService;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// Ai 服务生成工厂,用于创建 Ai 服务实例.
@Configuration
public class AiServiceFaction {

    @Resource
    private StreamingChatModel streamingChatModel;

    @Resource
    private ChatModel chatModel;
    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Bean
    public AiService aiCodeGenerate() {
        return AiServices.builder(AiService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .chatMemoryProvider(memoryId -> {   // ✅ 每次 AI 调用时触发
                    Long appId = Long.parseLong((String) memoryId);

                    MessageWindowChatMemory memory = MessageWindowChatMemory
                            .builder()
                            .id(memoryId)
                            .chatMemoryStore(redisChatMemoryStore)
                            .maxMessages(20)
                            .build();

                    chatHistoryService.loadChatHistoryToMemory(appId, memory, 20);
                    return memory;
                })
                .build();
    }
}
