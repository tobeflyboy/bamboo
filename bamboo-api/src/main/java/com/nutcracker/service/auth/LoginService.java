package com.nutcracker.service.auth;


import com.nutcracker.entity.domain.auth.AuthToken;
import com.nutcracker.entity.domain.auth.CaptchaInfo;
import com.nutcracker.entity.dto.WxMiniAppCodeLoginDTO;
import com.nutcracker.entity.dto.WxMiniAppPhoneLoginDTO;

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

    /**
     * 微信小程序登录
     *
     * @param code 微信登录code
     * @return 登录结果
     */
    AuthToken loginByWechat(String code);

    /**
     * 微信小程序Code登录
     *
     * @param loginDTO 登录参数
     * @return 访问令牌
     */
    AuthToken loginByWxMiniAppCode(WxMiniAppCodeLoginDTO loginDTO);

    /**
     * 微信小程序手机号登录
     *
     * @param loginDTO 登录参数
     * @return 访问令牌
     */
    AuthToken loginByWxMiniAppPhone(WxMiniAppPhoneLoginDTO loginDTO);

    /**
     * 发送短信验证码
     *
     * @param mobile 手机号
     */
    void sendSmsLoginCode(String mobile);

    /**
     * 短信验证码登录
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return 登录结果
     */
    AuthToken loginBySms(String mobile, String code);
}
