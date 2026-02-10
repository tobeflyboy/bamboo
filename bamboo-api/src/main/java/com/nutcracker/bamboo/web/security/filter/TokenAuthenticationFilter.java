package com.nutcracker.bamboo.web.security.filter;

import java.io.IOException;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nutcracker.bamboo.common.constant.SecurityConstants;
import com.nutcracker.bamboo.common.util.ResponseUtils;
import com.nutcracker.bamboo.common.wrapper.ResultCode;
import com.nutcracker.bamboo.domain.model.valueobject.OnlineUser;
import com.nutcracker.bamboo.web.security.service.TokenService;

import cn.hutool.core.util.StrUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Token 认证校验过滤器
 *
 * @author wangtao
 * @since 2025/3/6 16:50
 */
@Slf4j
@RequiredArgsConstructor
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    /**
     * Token 管理器
     */
    private final TokenService tokenService;


    /**
     * 校验 Token ，包括验签和是否过期
     * 如果 Token 有效，将 Token 解析为 Authentication 对象，并设置到 Spring Security 上下文中
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        log.debug("Authorization={}", authorizationHeader);
        try {
            // 跳过 OPTIONS 请求
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                filterChain.doFilter(request, response);
                return;
            }

            if (StrUtil.isNotBlank(authorizationHeader) && authorizationHeader.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {

                // 剥离Bearer前缀获取原始令牌
                String rawToken = authorizationHeader.substring(SecurityConstants.BEARER_TOKEN_PREFIX.length());

                // 执行令牌有效性检查（包含密码学验签和过期时间验证）
                boolean isValidToken = tokenService.validateToken(rawToken);
                if (!isValidToken) {
                    log.warn("TokenAuthenticationFilter 访问令牌无效或已过期 uri={}", request.getRequestURI());
                    ResponseUtils.writeErrMsg(response, ResultCode.ACCESS_TOKEN_INVALID);
                    return;
                }

                // 将令牌解析为 Spring Security 上下文认证对象
                Authentication authentication = tokenService.parseToken(rawToken);
                log.info("Parsed Authentication: principal={}, authorities={}", authentication.getPrincipal(), authentication.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
                // 同时设置ThreadLocal变量，确保Identify.getSessionUser()也能获取到用户信息
                Object principal = authentication.getPrincipal();
                if (principal instanceof OnlineUser) {
                    com.nutcracker.bamboo.web.Identify.setSessionUser((OnlineUser) principal);
                    log.debug("已设置ThreadLocal用户信息: userId={}", ((OnlineUser) principal).getUserId());
                }
                
                log.info("setAuthentication done.");
            }
        } catch (Exception ex) {
            // 安全上下文清除保障（防止上下文残留）
            SecurityContextHolder.clearContext();
            log.warn("TokenAuthenticationFilter 访问令牌无效或已过期 uri={}", request.getRequestURI(), ex);
            ResponseUtils.writeErrMsg(response, ResultCode.ACCESS_TOKEN_INVALID);
            return;
        }

        // 继续后续过滤器链执行
        filterChain.doFilter(request, response);
    }
}
