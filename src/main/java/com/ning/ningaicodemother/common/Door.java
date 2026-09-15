package com.ning.ningaicodemother.common;


import com.ning.ningaicodemother.ai.core.parse.CodeParserExecutor;
import com.ning.ningaicodemother.ai.core.saver.CodeFileSaverExecutor;
import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import com.ning.ningaicodemother.ai.model.HtmlResult;
import com.ning.ningaicodemother.ai.model.MultiResult;
import com.ning.ningaicodemother.ai.service.AiService;
import com.ning.ningaicodemother.exception.BusinessException;
import com.ning.ningaicodemother.enums.ErrorCode;
import com.ning.ningaicodemother.exception.ThrowUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.springframework.util.StreamUtils;

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
    public File generateCode(String userPrompt, CodeTypeEnum codeTypeEnum,Long appid){
        ThrowUtils.throwIf(codeTypeEnum==null, ErrorCode.PARAMS_ERROR);
      return  switch (codeTypeEnum) {
            case HTML->{
                HtmlResult htmlResult=aiService.generateHtmlCode(userPrompt);
                yield CodeFileSaverExecutor.executeSaver(htmlResult,codeTypeEnum,appid);
            }
            case MULTI_FILE->{
                 MultiResult multiResult = aiService.generateMultiHtmlCode(userPrompt);
                 yield CodeFileSaverExecutor.executeSaver(multiResult,codeTypeEnum,appid);
            }
            default->throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"没有对应的生成类");
        };
    }

    /**
     * 同上,但是sse流式输出
     * @param userPrompt
     * @param codeTypeEnum
     * @return 流式对象返回给前端,实现打字机效果.
     */
    public Flux<String> generateCodeStreaming(String userPrompt, CodeTypeEnum codeTypeEnum,Long appid) {
        ThrowUtils.throwIf(codeTypeEnum==null, ErrorCode.PARAMS_ERROR);
        String systemPrompt = readSystemPrompt(codeTypeEnum);
        String combinedPrompt = systemPrompt + "\n\n---\n\n用户需求:\n" + userPrompt;
        log.info("===== 本次流式生成拼接后的完整 Prompt =====\n{}", combinedPrompt);
        return switch (codeTypeEnum) {
            case HTML->{
                Flux<String> stringFlux = aiService.generateHtmlCodeStreaming(combinedPrompt);
                yield generateCodeStreaming(stringFlux,codeTypeEnum,appid);
            }
            case MULTI_FILE->{
                Flux<String> stringFlux = aiService.generateMultiHtmlCodeStreaming(combinedPrompt);
                yield generateCodeStreaming(stringFlux,codeTypeEnum,appid);
            }
            default->throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"没有对应的生成类");
        };
    }

    private String readSystemPrompt(CodeTypeEnum codeTypeEnum) {
        String resourcePath = switch (codeTypeEnum) {
            case HTML -> "prompt/codegen-file-system-prompt.txt";
            case MULTI_FILE -> "prompt/codegen-multi-file-system-prompt.txt";
        };
        try {
            String content = StreamUtils.copyToString(
                    getClass().getClassLoader().getResourceAsStream(resourcePath),
                    StandardCharsets.UTF_8
            );
            log.info("===== 读取到 System Prompt ({}):=====\n{}", resourcePath, content);
            return content;
        } catch (IOException | NullPointerException e) {
            log.error("读取 System Prompt 文件失败: {}", resourcePath, e);
            return "";
        }
    }




    /**
     * 处理流式的通用方法
     * @param stringFlux
     * @param codeTypeEnum
     * @return
     */
    private Flux<String> generateCodeStreaming(Flux<String> stringFlux, CodeTypeEnum codeTypeEnum,Long appid) {
        StringBuilder codeBuilder = new StringBuilder();
        return stringFlux
                .doOnNext(codeBuilder::append)
                .doOnComplete(()->{
                    try{
                        String string = codeBuilder.toString();
                        log.info("AI 流式完整输出:\n{}", string);
                        Object executor = CodeParserExecutor.executor(string, codeTypeEnum);
                        File file = CodeFileSaverExecutor.executeSaver(executor, codeTypeEnum,appid);
                        log.info("流式生成成功: {}",file.getAbsoluteFile());
                    }catch (Exception e){
                        log.error("流式生成失败: {}", e.getMessage(), e);
                    }
                });
    }


}