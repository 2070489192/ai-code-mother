package com.ning.ningaicodemother.common;

import com.ning.ningaicodemother.exception.ErrorCode;
import lombok.Data;

import java.io.Serializable;

// 基础响应类
@Data
public class BaseResponse<T> implements Serializable {
    private static final long serializableUID=1L;

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }


    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
