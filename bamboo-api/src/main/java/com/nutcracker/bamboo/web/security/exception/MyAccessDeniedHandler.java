package com.nutcracker.bamboo.web.security.exception;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.nutcracker.bamboo.common.util.ResponseUtils;
import com.nutcracker.bamboo.common.wrapper.ResultCode;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * 无权限访问处理器
 *
 * @author 胡桃夹子
 * @since 2.0.0
 */
@Slf4j
public class MyAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) {
        log.warn("MyAccessDeniedHandler 访问未授权");
        ResponseUtils.writeErrMsg(response, ResultCode.ACCESS_UNAUTHORIZED);
    }

}
