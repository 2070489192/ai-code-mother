package com.ning.ningaicodemother.pojo;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum UserCode {
    USER("用户", "user"),
    ADMIN("管理员", "admin");
    private final String text;
    private final String value;

   UserCode(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static UserCode getCodeByValue(String value) {
       if(value == null) {
           return null;
       }
       if(ObjUtil.isEmpty(value)) {
           return null;
       }
       for(UserCode code : UserCode.values()) {
           if(code.getValue().equals(value)) {
               return code;
           }
       }
       return null;
    }
}
