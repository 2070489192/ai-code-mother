package com.ning.ningaicodemother.ai.core.parse;

import com.ning.ningaicodemother.ai.model.HtmlResult;
import com.ning.ningaicodemother.exception.BusinessException;
import com.ning.ningaicodemother.enums.ErrorCode;


public class HtmlCodeParser implements CodeParser<HtmlResult> {

    @Override
    public HtmlResult parse(String codeContent) {
        String content = safeTrim(codeContent);
        HtmlResult result = new HtmlResult();
        String htmlCode = extractCodeByPattern(content, HTML_CODE_PATTERN);
        if (isEmpty(htmlCode)) {
            throw  new BusinessException(ErrorCode.OPERATION_ERROR,"未找到HTML代码块");
        }
        result.setHtmlCode(htmlCode.trim());
        return result;
    }
}
