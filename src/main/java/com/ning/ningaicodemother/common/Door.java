package com.ning.ningaicodemother.common;


import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import com.ning.ningaicodemother.ai.model.HtmlResult;
import com.ning.ningaicodemother.ai.model.MultiResult;
import com.ning.ningaicodemother.ai.service.AiService;
import com.ning.ningaicodemother.ai.utils.AiCodeWrite;
import com.ning.ningaicodemother.ai.utils.CodeParser;
import com.ning.ningaicodemother.exception.BusinessException;
import com.ning.ningaicodemother.exception.ErrorCode;
import com.ning.ningaicodemother.exception.ThrowUtils;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.File;

@Slf4j
@Component
public class Door {
    private final  AiService aiService;

    @Autowired
    public Door(AiService aiService) {
        this.aiService=aiService;
    }

    /**
     * 根据用户提示词和代码类型,生成对应的代码文件(结构化输出)
     * @param userPrompt  用户提示词
     * @param codeTypeEnum   枚举类,通过传递的枚举,决定要生成什么类型的文件.
     * @return 文件
     */
    public File generateCode(String userPrompt, CodeTypeEnum codeTypeEnum){
        ThrowUtils.throwIf(codeTypeEnum==null, ErrorCode.PARAMS_ERROR);

      return  switch (codeTypeEnum) {
            case HTML->generateHtmlCode(userPrompt);
            case MULTI_FILE->generateMultiHtmlCode(userPrompt);
            default->throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"没有对应的生成类");
        };
    }

    /**
     * 同上,但是sse流式输出
     * @param userPrompt
     * @param codeTypeEnum
     * @return 流式对象返回给前端,实现打字机效果.
     */
    public Flux<String> generateCodeStreaming(String userPrompt, CodeTypeEnum codeTypeEnum) {
        ThrowUtils.throwIf(codeTypeEnum==null, ErrorCode.PARAMS_ERROR);
        return switch (codeTypeEnum) {
            case HTML->generateHtmlCodeStreaming(userPrompt);
            case MULTI_FILE->generateMultiHtmlCodeStreaming(userPrompt);
            default->throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"没有对应的生成类");
        };
    }

    /**
     * 流式生成多文件并保存
     * @param userPrompt
     * @return
     */
    private Flux<String> generateMultiHtmlCodeStreaming(String userPrompt) {
        Flux<String> stringFlux = aiService.generateMultiHtmlCodeStreaming(userPrompt);
        StringBuilder codeBuilder = new StringBuilder();
        return stringFlux
                .doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    String string = codeBuilder.toString();
                    if (StrUtil.isBlank(string)) {
                        // 流式没有拿到内容(推理模型把 token 全花在思考上),回退到非流式结构化生成,保证文件能保存
                        log.warn("多文件流式未返回内容,自动回退到非流式结构化生成");
                        generateMultiHtmlCode(userPrompt);
                        return;
                    }
                    MultiResult multiResult = CodeParser.parseMultiFileCode(string);
                    if (StrUtil.isBlank(multiResult.getCssCode()) || StrUtil.isBlank(multiResult.getJavascriptCode())) {
                        log.warn("多文件流式解析完成,但 css/js 为空,生成的网页可能缺少样式或脚本");
                    }
                    AiCodeWrite.saveMultiFile(multiResult);
                })
                .onErrorResume(e -> {
                    log.error("多文件流式生成失败: {}", e.getMessage(), e);
                    return Flux.error(new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 流式生成被中断,请重试"));
                });
    }


    /**
     * 流式生成原生HTML文件,并保存
     * @param userPrompt
     * @return
     */
    private Flux<String> generateHtmlCodeStreaming(String userPrompt) {
        Flux<String> stringFlux = aiService.generateHtmlCodeStreaming(userPrompt);
        StringBuilder codeBuilder = new StringBuilder();
        return stringFlux
                .doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    String string = codeBuilder.toString();
                    if (StrUtil.isBlank(string)) {
                        // 流式没有拿到内容,回退到非流式结构化生成,保证文件能保存
                        log.warn("单页 HTML 流式未返回内容,自动回退到非流式结构化生成");
                        generateHtmlCode(userPrompt);
                        return;
                    }
                    HtmlResult htmlResult = CodeParser.parseHtmlCode(string);
                    AiCodeWrite.saveHtmlFile(htmlResult);
                })
                .onErrorResume(e -> {
                    log.error("单页 HTML 流式生成失败: {}", e.getMessage(), e);
                    return Flux.error(new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 流式生成被中断,请重试"));
                });
    }

    /**
     *  生成多文件的结构化输出并保存
     * @param userPrompt
     * @return
     */
    private File generateMultiHtmlCode(String userPrompt) {
        MultiResult multiResult = aiService.generateMultiHtmlCode(userPrompt);
      return AiCodeWrite.saveMultiFile(multiResult);
    }

    /**
     * 生成原生HTML文件并且保存
     * @param userPrompt
     * @return
     */
    private File generateHtmlCode(String userPrompt) {
        HtmlResult htmlResult = aiService.generateHtmlCode(userPrompt);
        return AiCodeWrite.saveHtmlFile(htmlResult);
    }


}
