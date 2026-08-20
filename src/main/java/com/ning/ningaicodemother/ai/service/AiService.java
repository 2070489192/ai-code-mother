package com.ning.ningaicodemother.ai.service;


import com.ning.ningaicodemother.ai.model.HtmlResult;
import com.ning.ningaicodemother.ai.model.MultiResult;
import dev.langchain4j.service.SystemMessage;
import org.w3c.dom.html.HTMLAreaElement;
import reactor.core.publisher.Flux;

public interface AiService {
    /**
     *
     * @param userPrompt 用户提示词
     * @return Html单页面
     */
    @SystemMessage(fromResource = "prompt/codegen-file-system-prompt.txt")
    HtmlResult generateHtmlCode(String userPrompt);

    /**
     *
     * @param userPrompt 用户提示词
     * @return 多页面
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MultiResult generateMultiHtmlCode(String userPrompt);

    /**
     * 流式生成 Html 代码
     * @param userPrompt
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-file-system-prompt.txt")
    Flux<String> generateHtmlCodeStreaming(String userPrompt);

    /**
     * 流式生成 多文件的代码
     * @param userPrompt
     * @return
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String > generateMultiHtmlCodeStreaming(String userPrompt);
}
