package com.ning.ningaicodemother;

import com.ning.ningaicodemother.ai.model.HtmlResult;
import com.ning.ningaicodemother.ai.service.AiService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Slf4j
public class AiServiceTest {

    @Resource
    private AiService aiService;


    @Test
    public void testAiCodeGenerate(){
    HtmlResult htmlCode= aiService.generateHtmlCode("你好,请帮我制作一个宁科涵的个人博客");
    log.info(htmlCode.toString());
    }
}
