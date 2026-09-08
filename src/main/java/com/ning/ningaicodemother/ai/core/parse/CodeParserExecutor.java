package com.ning.ningaicodemother.ai.core.parse;

import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import com.ning.ningaicodemother.exception.BusinessException;
import com.ning.ningaicodemother.enums.ErrorCode;

public class CodeParserExecutor {
 private static final HtmlCodeParser htmlCodeParser=new HtmlCodeParser();
 private static final MultiCodeParser multiFileCodeParser=new MultiCodeParser();

    public static Object executor(String codeContent, CodeTypeEnum codeTypeEnum){
        return switch (codeTypeEnum){
            case HTML -> htmlCodeParser.parse(codeContent);
            case MULTI_FILE -> multiFileCodeParser.parse(codeContent);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR,"没有这样的参数");
        };

    }
}
