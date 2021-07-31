package com.ztq.filters;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理，防止抛给客户端异常信息
 */
@RestControllerAdvice
public class GlobalExeption {
    @ExceptionHandler(value = Exception.class)
    public String exceptionHandler(Exception e) {
        return "服务器内部错误";
    }
}
