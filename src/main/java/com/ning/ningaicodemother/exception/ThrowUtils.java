package com.ning.ningaicodemother.exception;

// 异常抛出工具类
public class ThrowUtils {


    /**
     * 无条件抛异常
     *
     * @param errorCode 错误码
     * @param message   错误信息
     * @return 无返回值
     */
    public static <T> T throwException(ErrorCode errorCode, String message) {
        throw new BusinessException(errorCode, message);
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition        条件
     * @param runtimeException 异常
     */
    public static void throwIf(boolean condition, RuntimeException runtimeException) {
        if (condition) {
            throw runtimeException;
        }
    }

    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param errorCode 错误码
     */
    public static void throwIf(boolean condition, ErrorCode errorCode) {
        throwIf(condition, new BusinessException(errorCode));
    }

    public static void throwIf(boolean condition ,int code ,String message){
        throwIf(condition,new BusinessException(code,message));
    }
    /**
     * 条件成立则抛异常
     *
     * @param condition 条件
     * @param errorCode 错误码
     * @param message   错误信息
     */
    public static void throwIf(boolean condition, ErrorCode errorCode, String message) {
        throwIf(condition, new BusinessException(errorCode, message));
    }
}
