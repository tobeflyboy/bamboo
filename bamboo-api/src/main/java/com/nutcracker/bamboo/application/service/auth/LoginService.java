package com.nutcracker.bamboo.application.service.auth;


import com.nutcracker.bamboo.domain.model.valueobject.AuthToken;
import com.nutcracker.bamboo.domain.model.valueobject.CaptchaInfo;

/**
 * 认证服务接口
 *
 * @author 胡桃夹子
 * @since 2.4.0
 */
public interface LoginService {

    /**
     * 登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 登录结果
     */
    AuthToken login(String username, String password);

    /**
     * 登出
     */
    void logout();

    /**
     * 获取验证码
     *
     * @return 验证码
     */
    CaptchaInfo getCaptcha();

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 登录结果
     */
    AuthToken refreshToken(String refreshToken);


}
