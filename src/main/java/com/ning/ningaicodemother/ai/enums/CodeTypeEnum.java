package com.ning.ningaicodemother.ai.enums;

import cn.hutool.core.util.ObjectUtil;
import com.ning.ningaicodemother.exception.BusinessException;
import com.ning.ningaicodemother.exception.ErrorCode;
import com.ning.ningaicodemother.exception.ThrowUtils;
import lombok.Getter;

@Getter
public enum CodeTypeEnum {
    HTML("原生HTML模式","html"),
    MULTI_FILE("原生多文件模式","multi_file");

    private final String text;
    private final String value;

    CodeTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }
    public static  CodeTypeEnum  getEnumByValue(String value) {

            ThrowUtils.throwIf(ObjectUtil.isEmpty(value), ErrorCode.PARAMS_ERROR,"value不能为空");

        for(CodeTypeEnum codeTypeEnum : CodeTypeEnum.values()){
            if(codeTypeEnum.getValue().equals(value)){
                return codeTypeEnum;
            }
        }
       return ThrowUtils.throwException(ErrorCode.PARAMS_ERROR,"未找到对应的枚举值");
    }
}
