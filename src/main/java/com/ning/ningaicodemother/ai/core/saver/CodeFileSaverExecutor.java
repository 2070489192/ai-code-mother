package com.ning.ningaicodemother.ai.core.saver;

import com.ning.ningaicodemother.ai.enums.CodeTypeEnum;
import com.ning.ningaicodemother.exception.BusinessException;
import com.ning.ningaicodemother.enums.ErrorCode;

import java.io.File;

/**
 * 代码文件保存执行器
 * 根据代码生成类型执行相应的保存逻辑
 *
 * @author yupi
 */
public class CodeFileSaverExecutor {

    private static final SaveHtml saveHtml = new SaveHtml();

    private static final SaveMulti saveMulti = new SaveMulti();

    /**
     * 执行代码保存
     *
     * @param codeResult  代码结果对象
     * @param codeTypeEnum 代码生成类型
     * @return 保存的目录
     */
    public static File executeSaver(Object codeResult, CodeTypeEnum codeTypeEnum,Long appid) {
        return switch (codeTypeEnum) {
            case HTML -> saveHtml.saveFile(codeResult,appid);
            case MULTI_FILE -> saveMulti.saveFile(codeResult,appid);
            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型: " + codeTypeEnum);
        };
    }
}
