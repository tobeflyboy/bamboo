package com.nutcracker.web;

import cn.hutool.core.text.CharSequenceUtil;
import com.nutcracker.common.exception.BusinessException;
import com.nutcracker.common.wrapper.ResultCode;
import com.nutcracker.common.wrapper.WrapperResp;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 家装转介-全局异常拦截
 *
 * @author 胡桃夹子
 * @date 2024-04-11 10:15
 */
@Slf4j
@RestControllerAdvice(basePackages = {"com.nutcracker.web.rest"})
public class GlobalExceptionHandler {

    // 拦截：参数错误
    @ExceptionHandler(IllegalArgumentException.class)
    public WrapperResp<Boolean> handlerException(IllegalArgumentException e) {
        log.error("未登录", e);
        // 返回给前端
        return WrapperResp.failed(e.getMessage());
    }

    // 拦截：未登录异常
    @ExceptionHandler(UsernameNotFoundException.class)
    public WrapperResp<Boolean> handlerException(UsernameNotFoundException e) {
        log.error("未登录", e);
        // 返回给前端
        return WrapperResp.failed(e.getMessage());
    }

    // @Valid注解数验证不通过，异常处理
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public WrapperResp<Boolean> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        FieldError firstFieldError = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .orElse(null);
        String errorMessage = firstFieldError != null ? firstFieldError.getDefaultMessage() : "Invalid request parameters";
        log.error("MethodArgumentNotValidException, message={}, stackTrace={}", errorMessage, e.getMessage());
        return WrapperResp.failed(errorMessage);
    }

    // 业务逻辑中手动进行数据验证时抛出ValidationException，异常处理
    @ExceptionHandler(ValidationException.class)
    public WrapperResp<Boolean> handleValidationException(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        List<String> collect = allErrors.stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
        String message = CharSequenceUtil.join(",", collect);
        log.error("handleValidationException，message={}， stackTrace={}", message, e.getMessage());
        return WrapperResp.failed(message);
    }

    // 拦截：业务异常
    @ExceptionHandler(BusinessException.class)
    public WrapperResp<Boolean> handlerBusinessException(BusinessException e) {
        log.warn("业务异常", e);
        if (e.getMessage().contains("用户未登录")) {
            return WrapperResp.failed(ResultCode.ACCESS_UNAUTHORIZED);
        }
        return WrapperResp.failed(e.getMessage());
    }

    // 全局异常拦截
    @ExceptionHandler
    public WrapperResp<Boolean> handlerException(Exception e) {
        log.error("全局异常拦截", e);
        return WrapperResp.failed(e.getMessage());
    }
}

