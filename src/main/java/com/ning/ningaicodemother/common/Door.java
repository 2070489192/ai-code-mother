package com.ning.ningaicodemother.common;


import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import com.ning.ningaicodemother.ai.model.HtmlResult;
import com.ning.ningaicodemother.ai.model.MultiResult;
import com.ning.ningaicodemother.ai.service.AiService;
import com.ning.ningaicodemother.ai.utils.AiCodeWrite;
import com.ning.ningaicodemother.exception.BusinessException;
import com.ning.ningaicodemother.exception.ErrorCode;
import com.ning.ningaicodemother.exception.ThrowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class Door {
    private  AiService aiService;

    @Autowired
    public Door(AiService aiService) {
        this.aiService=aiService;
    }

    //根据传递的类型不同,调用不同的生成方法.
    public File generateCode(String userPrompt, CodeTypeEnum codeTypeEnum){
        ThrowUtils.throwIf(codeTypeEnum==null, ErrorCode.PARAMS_ERROR);

      return  switch (codeTypeEnum) {
            case HTML->generateHtmlCode(userPrompt);
            case MULTI_FILE->generateMultiHtmlCode(userPrompt);
            default->throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"没有对应的生成类");
        };
    }

    private File generateMultiHtmlCode(String userPrompt) {
        MultiResult multiResult = aiService.generateMultiHtmlCode(userPrompt);
      return AiCodeWrite.saveMultiFile(multiResult);
    }

    private File generateHtmlCode(String userPrompt) {
        HtmlResult htmlResult = aiService.generateHtmlCode(userPrompt);
        return AiCodeWrite.saveHtmlFile(htmlResult);
    }
}
