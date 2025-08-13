package com.nutcracker.web.security.exception;

import com.nutcracker.common.wrapper.ResultCode;
import com.nutcracker.util.ResponseUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * 统一处理 Spring Security 认证失败响应
 *
 * @author 胡桃夹子
 */
@Slf4j
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 认证失败处理入口方法
     *
     * @param request 触发异常的请求对象（可用于获取请求头、参数等）
     * @param response 响应对象（用于写入错误信息）
     * @param authException 认证异常对象（包含具体失败原因）
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (authException instanceof BadCredentialsException) {
            // 用户名或密码错误
            log.warn("MyAuthenticationEntryPoint 用户名或密码错误, uri={}", request.getRequestURI(), authException);
            ResponseUtils.writeErrMsg(response, ResultCode.USER_PASSWORD_ERROR);
        } else if (authException instanceof InsufficientAuthenticationException) {
            // 请求头缺失Authorization、Token格式错误、Token过期、签名验证失败
            log.warn("MyAuthenticationEntryPoint 访问令牌无效或已过期, uri={}", request.getRequestURI(), authException);
            ResponseUtils.writeErrMsg(response, ResultCode.ACCESS_TOKEN_INVALID);
        } else {
            // 其他未明确处理的认证异常（如账户被锁定、账户禁用等）
            log.warn("MyAuthenticationEntryPoint 用户登录异常, uri={}", request.getRequestURI(), authException);
            ResponseUtils.writeErrMsg(response, ResultCode.USER_LOGIN_EXCEPTION, authException.getMessage());
        }
    }
}




