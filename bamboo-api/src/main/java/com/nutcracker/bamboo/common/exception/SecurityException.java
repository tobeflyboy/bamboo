package com.nutcracker.bamboo.common.exception;

/**
 * 安全相关领域异常
 *
 * @author 胡桃夹子
 * @since 2026-02-09
 */
public class SecurityException extends DomainException {
    
    public SecurityException(String message) {
        super("SECURITY_ERROR", message);
    }
    
    public SecurityException(String message, Throwable cause) {
        super("SECURITY_ERROR", message, cause);
    }
}