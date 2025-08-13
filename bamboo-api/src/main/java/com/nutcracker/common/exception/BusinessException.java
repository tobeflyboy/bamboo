package com.nutcracker.common.exception;

import com.nutcracker.common.wrapper.IResultCode;
import org.slf4j.helpers.MessageFormatter;

/**
 * 自定义异常类
 *
 * @author 胡桃夹子
 * @date 2020/2/21 18:08
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 5787303780395601031L;

    public IResultCode resultCode;

    public BusinessException(IResultCode errorCode) {
        super(errorCode.getMsg());
        this.resultCode = errorCode;
    }


    public BusinessException(IResultCode errorCode,String message) {
        super(message);
        this.resultCode = errorCode;
    }


    public BusinessException(String message, Throwable cause) {
        super(message, cause);
    }

    public BusinessException(Throwable cause) {
        super(cause);
    }

    public BusinessException(String message, Object... args) {
        super(formatMessage(message, args));
    }

    private static String formatMessage(String message, Object... args) {
        return MessageFormatter.arrayFormat(message, args).getMessage();
    }
}
