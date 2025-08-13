package com.nutcracker.common.wrapper;

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * Response Wrapper
 *
 * @author 胡桃夹子
 * @date 2025/04/03 10:32:05
 */
@ToString(callSuper = true)
@Getter
public class WrapperResp<T> implements Serializable {
    private static final long serialVersionUID = 4311633334311958565L;

    // Getter 方法
    private final String code;
    private final String msg;
    private final T data;
    private final long timestamp;

    // 构造函数私有化
    private WrapperResp(String code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    // 成功响应（无数据）
    public static <T> WrapperResp<T> success() {
        return new WrapperResp<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), null);
    }

    // 成功响应（带数据）
    public static <T> WrapperResp<T> success(T data) {
        return new WrapperResp<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMsg(), data);
    }

    // 成功响应（自定义消息）
    public static <T> WrapperResp<T> success(String msg, T data) {
        return new WrapperResp<>(ResultCode.SUCCESS.getCode(), msg, data);
    }

    // 失败响应
    public static <T> WrapperResp<T> failed(ResultCode code) {
        return new WrapperResp<>(code.getCode(), code.getMsg(), null);
    }

    // 失败响应（自定义消息）
    public static <T> WrapperResp<T> failed(String msg) {
        return new WrapperResp<>(ResultCode.SYSTEM_ERROR.getCode(), msg, null);
    }

    // 失败响应（自定义消息）
    public static <T> WrapperResp<T> failed(ResultCode code, String msg) {
        return new WrapperResp<>(code.getCode(), msg, null);
    }

    // 403 无权限
    public static <T> WrapperResp<T> unAuthorized(String msg) {
        return new WrapperResp<>(ResultCode.ACCESS_UNAUTHORIZED.getCode(), msg, null);
    }

    // 参数校验失败
    public static <T> WrapperResp<T> validateFailed(String msg) {
        return new WrapperResp<>(ResultCode.USER_REQUEST_PARAMETER_ERROR.getCode(), msg, null);
    }

    // 分页响应
    public static <T> WrapperResp<PageWrapperResp<T>> pageSuccess(List<T> list, long total) {
        return WrapperResp.success(new PageWrapperResp<>(list, total));
    }

}