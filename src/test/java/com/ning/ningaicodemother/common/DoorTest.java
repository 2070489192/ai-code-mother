package com.ning.ningaicodemother.common;

import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import com.ning.ningaicodemother.ai.model.MultiResult;
import com.ning.ningaicodemother.ai.service.AiService;
import com.ning.ningaicodemother.ai.utils.CodeParser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.List;

@SpringBootTest
@Slf4j
class DoorTest {
    @Autowired
    private  Door door;

    @Test
    void generateAndSaveCodeStream() {
        Flux<String> codeStream =door
                .generateCodeStreaming(
                        "帮我做一个王智的个人技术博客,擅长各种后端,做过黑马点评项目,不超过80行"
                        , CodeTypeEnum.MULTI_FILE);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 验证结果
        Assertions.assertNotNull(result);
        String completeContent = String.join("", result);
        log.info("AI 流式返回完整内容(共 {} 个分片):\n{}", result.size(), completeContent);
        Assertions.assertNotNull(completeContent);
    }

}