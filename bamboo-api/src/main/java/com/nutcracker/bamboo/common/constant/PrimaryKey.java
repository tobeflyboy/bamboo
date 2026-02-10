package com.nutcracker.bamboo.common.constant;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;

/**
 * 主键
 *
 * @author 胡桃夹子
 * @date 2025/09/04 15:43:21
 */
public final class PrimaryKey {

    private PrimaryKey() {

    }

    public static String getPermissionId() {
        return String.valueOf(IdWorker.getId("SYS_PERMISSION"));
    }

    public static String getRoleId() {
        return String.valueOf(IdWorker.getId("SYS_ROLE"));
    }

    public static String getRolePermissionId() {
        return String.valueOf(IdWorker.getId("SYS_ROLE_PERMISSION"));
    }

    public static String getSysUserId() {
        return String.valueOf(IdWorker.getId("SYS_USER"));
    }

    public static String getSysUserRoleId() {
        return String.valueOf(IdWorker.getId("SYS_USER_ROLE"));
    }

    public static String getConfigId() {
        return String.valueOf(IdWorker.getId("SYS_CONFIG"));
    }

    public static String getNewsId() {
        return String.valueOf(IdWorker.getId("NEWS"));
    }
}
