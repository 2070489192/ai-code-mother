package com.ning.ningaicodemother.ai.core.saver;

import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import com.ning.ningaicodemother.ai.model.HtmlResult;

import java.io.File;

public class SaveHtml extends AiCodeWrite  {
    @Override
    public File saveFile(Object result) {
        HtmlResult htmlResult= (HtmlResult)result;
        String dirPath =createDirPath(CodeTypeEnum.HTML.getValue());
        saveSingleFile("index.html",dirPath,htmlResult.getHtmlCode());
        return new File(dirPath);
    }
}
