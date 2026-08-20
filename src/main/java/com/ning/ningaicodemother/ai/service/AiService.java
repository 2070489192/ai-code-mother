package com.ning.ningaicodemother.ai.service;


import com.ning.ningaicodemother.ai.model.HtmlResult;
import com.ning.ningaicodemother.ai.model.MultiResult;
import dev.langchain4j.service.SystemMessage;
import org.w3c.dom.html.HTMLAreaElement;

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
}
