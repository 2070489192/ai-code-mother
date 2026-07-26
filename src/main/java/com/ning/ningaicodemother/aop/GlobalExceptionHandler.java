package com.ning.ningaicodemother.aop;


import com.ning.ningaicodemother.common.BaseResponse;
import com.ning.ningaicodemother.common.ResultUtil;
import com.ning.ningaicodemother.exception.BusinessException;
import com.ning.ningaicodemother.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// 全局异常处理类
@Hidden
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public BaseResponse<?> businessExceptionHandler( BusinessException e){
        log.error("业务异常,{}",e.getMessage());
        return ResultUtil.error(e.getCode(),e.getMessage());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse<?> runtimeExceptionHandler( RuntimeException e){
        log.error("系统异常",e);
        return ResultUtil.error(ErrorCode.SYSTEM_ERROR);
    }

}
