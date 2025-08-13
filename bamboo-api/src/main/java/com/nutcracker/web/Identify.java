package com.nutcracker.web;

import com.nutcracker.entity.domain.auth.OnlineUser;
import lombok.extern.slf4j.Slf4j;

/**
 * 用于存储用户信息
 *
 * @author 胡桃夹子
 * @date 2025/03/20 14:05:58
 */
@Slf4j
public class Identify {

    private Identify() {

    }

    private static final ThreadLocal<OnlineUser> SESSION_USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 缓存当前登录人
     *
     * @param onlineUser 用户
     */
    public static void setSessionUser(OnlineUser onlineUser) {
        SESSION_USER_THREAD_LOCAL.set(onlineUser);
    }

    /**
     * 获取当前用户
     *
     * @return {@link OnlineUser }
     */
    public static OnlineUser getSessionUser() {
        return SESSION_USER_THREAD_LOCAL.get();
    }

    /**
     * 清除当前用户
     */
    public static void clearSessionUser() {
        OnlineUser currentOnlineUser = SESSION_USER_THREAD_LOCAL.get();
        if (currentOnlineUser != null) {
            SESSION_USER_THREAD_LOCAL.remove();
        }
    }

}
