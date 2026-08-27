package com.ning.ningaicodemother.ai.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;


import java.io.File;
import java.nio.charset.StandardCharsets;

public abstract class AiCodeWrite {
    //指定根目录
    private static final String ROOT_PATH = System.getProperty("user.dir")+ File.separator+"tmp/out_code";

    //指定文件夹路径
   protected static String  createDirPath(String bizType){
        String unique= StrUtil.format("{}_{}",bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath= ROOT_PATH+File.separator+unique;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }
    //保存单文件代码
    protected static void saveSingleFile(String fileName,String dirPath,String content){
        String filePath = dirPath+File.separator+fileName;
        //null 防护,避免 AI 输出为空时写文件报 NPE
        FileUtil.writeString(content == null ? "" : content, filePath, StandardCharsets.UTF_8);
    }
    //保存代码
    public abstract File saveFile(Object result);

}
