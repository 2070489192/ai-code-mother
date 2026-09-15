package com.ning.ningaicodemother.ai.core.saver;

import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import com.ning.ningaicodemother.ai.model.MultiResult;

import java.io.File;

public class SaveMulti extends AiCodeWrite {
    @Override
    public  File saveFile(Object result,Long appid) {
        MultiResult multiResult= (MultiResult)result;
        String dirPath =createDirPath(CodeTypeEnum.MULTI_FILE.getValue(), appid);
        saveSingleFile("index.html",dirPath,multiResult.getHtmlCode());
        saveSingleFile("style.css",dirPath,multiResult.getCssCode());
        saveSingleFile("script.js",dirPath,multiResult.getJavascriptCode());
        return new File(dirPath);
    }
}
