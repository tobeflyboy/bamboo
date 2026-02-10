package com.nutcracker.bamboo.common.enums;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.Getter;
import lombok.ToString;

/**
 * 权限隐藏枚举
 *
 * @author 胡桃夹子
 * @date 2025/02/07 10:39:11
 */
@Getter
@ToString
public enum PermissionHideEnum {

    SHOW(1, "显示"),
    HIDE(0, "隐藏");

    private final Integer code;
    private final String msg;

    private final static Map<Integer, PermissionHideEnum> MAP = new LinkedHashMap<>();

    private PermissionHideEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    static {
        for (PermissionHideEnum statusEnum : PermissionHideEnum.values()) {
            MAP.put(statusEnum.getCode(), statusEnum);
        }
    }

    public static String of(Integer status) {
        for (PermissionHideEnum statusEnum : PermissionHideEnum.values()) {
            if (statusEnum.getCode().equals(status)) {
                return statusEnum.getMsg();
            }
        }
        return null;
    }

    public static Map<Integer, PermissionHideEnum> getHideMap() {
        return MAP;
    }
}
