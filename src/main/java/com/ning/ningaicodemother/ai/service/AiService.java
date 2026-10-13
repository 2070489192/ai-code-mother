package com.ning.ningaicodemother.ai.service;


import com.ning.ningaicodemother.ai.model.HtmlResult;
import com.ning.ningaicodemother.ai.model.MultiResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;

import reactor.core.publisher.Flux;

public interface AiService {
    /**
     *
     * @param userPrompt 用户提示词
     * @return Html单页面
     */
    @SystemMessage(fromResource = "prompt/codegen-file-system-prompt.txt")
    HtmlResult generateHtmlCode(@MemoryId String memoryId, @UserMessage String userPrompt);

    /**
     * 非流式(结构化输出)生成多文件代码,使用独立的系统提示词
     *
     * @param userPrompt 用户提示词
     * @return 多页面
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-structured-system-prompt.txt")
    MultiResult generateMultiHtmlCode(@MemoryId String memoryId, @UserMessage String userPrompt);

    /**
     * 流式生成 Html 代码
     * @param userPrompt
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-file-system-prompt.txt")
    Flux<String> generateHtmlCodeStreaming(@MemoryId String memoryId, @UserMessage String userPrompt);

    /**
     * 流式生成 多文件的代码
     * @param userPrompt
     * @return
     */

    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generateMultiHtmlCodeStreaming(@MemoryId String memoryId, @UserMessage String userPrompt);
}