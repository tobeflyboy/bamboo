package com.nutcracker.web.filter;

import com.nutcracker.config.property.SecurityProperties;
import com.nutcracker.util.IPUtils;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 请求日志过滤器
 * <p>
 * 功能：
 * <ul>
 * <li>记录请求方法、URI、查询参数（脱敏）、客户端IP</li>
 * <li>记录响应状态码、处理耗时</li>
 * <li>自动跳过文件上传请求（multipart）和配置的排除路径</li>
 * <li>敏感参数自动脱敏（如 password）</li>
 * <li>避免记录大请求体内容</li>
 * </ul>
 * </p>
 *
 * @author 胡桃夹子
 * @date 2025/08/11 13:36:43
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class RequestLogFilter extends CommonsRequestLoggingFilter {

    private final SecurityProperties securityProperties;
    private final PathMatcher pathMatcher = new AntPathMatcher();

    // 敏感参数关键词（用于脱敏）
    private static final Set<String> SENSITIVE_PARAM_KEYS = Set.of("password");

    // 文件上传请求的 Content-Type 前缀
    private static final String MULTIPART_PREFIX = "multipart/";

    // 请求开始时间属性名
    private static final String ATTR_START_TIME = "REQUEST_LOG_START_TIME";

    /**
     * 初始化日志过滤器配置
     */
    @PostConstruct
    public void init() {
        // 最大记录 10KB 请求体（实际不启用）
        setMaxPayloadLength(10240);
        // 不记录请求体（避免性能问题）
        setIncludePayload(true);
        // 不记录请求头
        setIncludeHeaders(true);
        // 记录查询字符串
        setIncludeQueryString(false);
        // 记录客户端 IP
        setIncludeClientInfo(true);
        // 日志前缀为空，自定义格式
        setBeforeMessagePrefix("");
    }

    /**
     * 判断是否应记录日志
     * 跳过文件上传请求
     */
    @Override
    protected boolean shouldLog(HttpServletRequest request) {
        return log.isInfoEnabled() && !isMultipartRequest(request);
    }

    /**
     * 请求处理前：记录请求基本信息
     */
    @Override
    protected void beforeRequest(HttpServletRequest request, String message) {
        if (isMultipartRequest(request)) {
            return;
        }

        // 记录开始时间
        request.setAttribute(ATTR_START_TIME, System.currentTimeMillis());

        String uri = request.getRequestURI();
        String clientIp = IPUtils.getIpAddr(request);

        // ✅ 获取所有参数（兼容 GET、POST form、JSON）
        String allParams = extractAllParameters(request);

        StringBuilder logPart = new StringBuilder();
        logPart.append(request.getMethod()).append(" ").append(uri);

        if (StringUtils.hasText(allParams)) {
            logPart.append(" params=").append(sanitizeQueryParams(allParams));
        }

        log.info(">> {} [client={}]", logPart, clientIp);
    }

    /**
     * 智能提取所有请求参数
     * 1. 优先使用 getParameterMap()（适用于 GET、x-www-form-urlencoded）
     * 2. 如果没有参数，且是 JSON/XML，尝试读 body
     */
    private String extractAllParameters(HttpServletRequest request) {
        // 1️⃣ 先尝试从 getParameter 获取（GET 查询参数 + x-www-form-urlencoded）
        StringBuilder formParams = new StringBuilder();
        Enumeration<String> paramNames = request.getParameterNames();
        boolean hasFormParams = false;

        while (paramNames.hasMoreElements()) {
            hasFormParams = true;
            String name = paramNames.nextElement();
            String[] values = request.getParameterValues(name);
            for (String value : values) {
                if (!formParams.isEmpty()) {
                    formParams.append("&");
                }
                formParams.append(name).append("=").append(value != null ? value : "");
            }
        }

        if (hasFormParams) {
            // ✅ 表单或 GET 参数
            return formParams.toString();
        }

        // 2️⃣ 否则尝试读 body（JSON、XML 等）
        String contentType = request.getContentType();
        if (contentType != null && (
                contentType.contains("application/json")
                        || contentType.contains("application/xml")
                        || contentType.contains("text/"))) {

            if (request instanceof ContentCachingRequestWrapper wrapper) {
                byte[] buf = wrapper.getContentAsByteArray();
                if (buf.length > 0) {
                    try {
                        return new String(buf, StandardCharsets.UTF_8);
                    } catch (Exception e) {
                        log.warn("Failed to read JSON/XML body", e);
                        return "<invalid_body>";
                    }
                }
            }
        }

        // 3️⃣ fallback：只返回 query string（理论上不会走到这）
        return request.getQueryString() != null ? request.getQueryString() : "";
    }

    /**
     * 请求处理后：记录响应状态和耗时
     */
    @Override
    protected void afterRequest(HttpServletRequest request, String message) {
        if (isMultipartRequest(request)) {
            return;
        }

        Long startTime = (Long) request.getAttribute(ATTR_START_TIME);
        if (startTime == null) {
            return;
        }

        long costTime = System.currentTimeMillis() - startTime;
        int statusCode = getResponseStatus(request);

        log.info("<< {} [status={}, time={}ms]", request.getRequestURI(), statusCode, costTime);
    }

    /**
     * 是否应跳过此请求的过滤（如静态资源、健康检查等）
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = getRelativePath(request);
        return matchesExclusionPattern(uri);
    }

    /**
     * 获取请求的相对路径（去除 contextPath）
     */
    private String getRelativePath(HttpServletRequest request) {
        String requestUri = request.getRequestURI();
        String contextPath = request.getContextPath();
        if (StringUtils.hasText(contextPath) && requestUri.startsWith(contextPath)) {
            return requestUri.substring(contextPath.length());
        }
        return requestUri;
    }

    /**
     * 检查路径是否匹配排除列表中的任意模式
     */
    private boolean matchesExclusionPattern(String uri) {
        String[] exclusions = securityProperties.getUnsecuredUrls();
        if (exclusions == null || exclusions.length == 0) {
            return false;
        }

        return Arrays.stream(exclusions)
                .filter(StringUtils::hasText)
                .anyMatch(pattern -> pathMatcher.match(pattern, uri));
    }

    /**
     * 查询参数脱敏处理
     * 例如：password=123456 → password=***
     */
    private String sanitizeQueryParams(String queryString) {
        return Arrays.stream(queryString.split("&"))
                .map(this::sanitizeQueryParam)
                .collect(Collectors.joining("&"));
    }

    /**
     * 对单个参数进行脱敏判断
     */
    private String sanitizeQueryParam(String param) {
        int equalsIndex = param.indexOf('=');
        if (equalsIndex <= 0) {
            // 无效参数格式
            return param;
        }

        String key = param.substring(0, equalsIndex).toLowerCase();
        if (isSensitiveParam(key)) {
            return param.substring(0, equalsIndex + 1) + "***";
        }
        return param;
    }

    /**
     * 判断参数名是否为敏感字段
     */
    private boolean isSensitiveParam(String paramKey) {
        return SENSITIVE_PARAM_KEYS.stream().anyMatch(sensitive ->
                paramKey.contains(sensitive) || paramKey.endsWith("[" + sensitive + "]"));
    }

    /**
     * 判断是否为文件上传请求
     */
    private boolean isMultipartRequest(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && contentType.startsWith(MULTIPART_PREFIX);
    }

    /**
     * 获取响应状态码
     * 优先从 ERROR_STATUS_CODE 属性获取，否则认为是 200
     */
    private int getResponseStatus(HttpServletRequest request) {
        Integer status = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        return status != null ? status : HttpServletResponse.SC_OK;
    }
}