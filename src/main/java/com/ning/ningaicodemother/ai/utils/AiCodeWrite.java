package com.ning.ningaicodemother.ai.utils;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import com.ning.ningaicodemother.ai.model.HtmlResult;
import com.ning.ningaicodemother.ai.model.MultiResult;


import java.io.File;
import java.nio.charset.StandardCharsets;

public class AiCodeWrite {
    //指定根目录
    private static final String ROOT_PATH = System.getProperty("user.dir")+ File.separator+"tmp/out_code";

    //指定文件夹路径
    private static String  createDirPath(String bizType){
        String unique= StrUtil.format("{}_{}",bizType, IdUtil.getSnowflakeNextIdStr());
       String dirPath= ROOT_PATH+File.separator+unique;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }
    //保存单文件代码
    private static void saveSingleFile(String fileName,String dirPath,String content){
        String filePath = dirPath+File.separator+fileName;
          FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
    //保存原生HTML代码
    public static File saveHtmlFile(HtmlResult htmlResult){
        String dirPath =createDirPath(CodeTypeEnum.HTML.getValue());
        saveSingleFile("index.html",dirPath,htmlResult.getHtmlCode());
        return new File(dirPath);
}
    //保存多文件代码
    public static File saveMultiFile(MultiResult multiResult){
        String dirPath =createDirPath(CodeTypeEnum.MULTI_FILE.getValue());
        saveSingleFile("index.html",dirPath,multiResult.getHtmlCode());
        saveSingleFile("style.css",dirPath,multiResult.getCssCode());
        saveSingleFile("script.js",dirPath,multiResult.getJavascriptCode());
        return new File(dirPath);
    }
}




