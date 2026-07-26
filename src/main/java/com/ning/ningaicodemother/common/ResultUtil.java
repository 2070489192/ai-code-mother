package com.ning.ningaicodemother.common;

import com.ning.ningaicodemother.exception.ErrorCode;

// 响应工具类
public class ResultUtil {

    public static<T> BaseResponse<T> success(T data){
        return new BaseResponse<>(0, data,"ok");
    }

    public static BaseResponse<?> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode);

    }

    public static BaseResponse<?> error(int code , String message){
        return new BaseResponse<>(code,null,message);
    }
}
