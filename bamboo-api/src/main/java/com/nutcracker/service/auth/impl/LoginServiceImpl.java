package com.nutcracker.service.auth.impl;

import cn.hutool.core.util.StrUtil;
import com.nutcracker.common.exception.BusinessException;
import com.nutcracker.common.wrapper.ResultCode;
import com.nutcracker.constant.RedisConstants;
import com.nutcracker.constant.SecurityConstants;
import com.nutcracker.entity.convert.auth.SysUserConvert;
import com.nutcracker.entity.domain.auth.AuthToken;
import com.nutcracker.entity.domain.auth.CaptchaInfo;
import com.nutcracker.entity.domain.auth.OnlineUser;
import com.nutcracker.entity.domain.auth.SysUser;
import com.nutcracker.entity.dto.WxMiniAppCodeLoginDTO;
import com.nutcracker.entity.dto.WxMiniAppPhoneLoginDTO;
import com.nutcracker.service.auth.LoginService;
import com.nutcracker.service.auth.SysUserService;
import com.nutcracker.util.JSON;
import com.nutcracker.web.security.extension.sms.SmsAuthenticationToken;
import com.nutcracker.web.security.extension.wx.WxMiniAppCodeAuthenticationToken;
import com.nutcracker.web.security.extension.wx.WxMiniAppPhoneAuthenticationToken;
import com.nutcracker.web.security.service.TokenService;
import com.nutcracker.web.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 *
 * @author 胡桃夹子
 * @since 2.4.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final SysUserService sysUserService;

    //private final Font captchaFont;
    //private final CaptchaProperties captchaProperties;
    //private final CodeGenerator codeGenerator;

    //private final SmsService smsService;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户名密码登录
     *
     * @param username 用户名
     * @param password 密码
     * @return 访问令牌
     */
    @Override
    public AuthToken login(String username, String password) {
        log.debug("login username={},password={}", username, password);

        SysUser sysUser = sysUserService.findByUsername(username);
        if (null == sysUser) {
            log.error("login error, 账号不存在！username={}", username);
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }

        String pwd = SecurityUtils.encryptPassword(sysUser.getSalt(), password, sysUser.getUsername());
        if (!StrUtil.equals(pwd, sysUser.getPassword())) {
            log.error("login error, 密码错误！username={},password={},pwd={}", username, sysUser.getPassword(), pwd);
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }
        // 手动创建已认证的 Authentication
        OnlineUser onlineUser = SysUserConvert.INSTANCE.toOnlineUser(sysUser);
        log.debug("login onlineUser={}", JSON.toJSONString(onlineUser));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, Collections.singletonList(new SimpleGrantedAuthority(sysUser.getRoleCode())));
        // 3. 认证成功后生成 JWT 令牌，并存入 Security 上下文，供登录日志 AOP 使用（已认证）
        AuthToken authenticationTokenResponse = tokenService.generateToken(onlineUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        authenticationTokenResponse.setOnlineUser(onlineUser);
        return authenticationTokenResponse;
    }

    /**
     * 微信一键授权登录
     *
     * @param code 微信登录code
     * @return 访问令牌
     */
    @Override
    public AuthToken loginByWechat(String code) {
        // 1. 创建用户微信认证的令牌（未认证）
        WxMiniAppCodeAuthenticationToken authenticationToken = new WxMiniAppCodeAuthenticationToken(code);

        // 2. 执行认证（认证中）
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        OnlineUser onlineUser = (OnlineUser) authentication.getPrincipal();
        //SysUser user = sysUserService.findByUsername(username);
        //OnlineUser onlineUser = SysUserConvert.INSTANCE.toOnlineUser(user);

        // 3. 认证成功后生成 JWT 令牌，并存入 Security 上下文，供登录日志 AOP 使用（已认证）
        AuthToken token = tokenService.generateToken(onlineUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return token;
    }

    /**
     * 发送登录短信验证码
     *
     * @param mobile 手机号
     */
    @Override
    public void sendSmsLoginCode(String mobile) {

        // 随机生成4位验证码
        // String code = String.valueOf((int) ((Math.random() * 9 + 1) * 1000));
        // TODO 为了方便测试，验证码固定为 1234，实际开发中在配置了厂商短信服务后，可以使用上面的随机验证码
        String code = "1234";

        // 发送短信验证码
        Map<String, String> templateParams = new HashMap<>();
        templateParams.put("code", code);
        try {
            //smsService.sendSms(mobile, SmsTypeEnum.LOGIN, templateParams);
            // TODO 发送短信
        } catch (Exception e) {
            log.error("发送短信验证码失败", e);
        }
        // 缓存验证码至Redis，用于登录校验
        redisTemplate.opsForValue().set(StrUtil.format(RedisConstants.Captcha.SMS_LOGIN_CODE, mobile), code, 5, TimeUnit.MINUTES);
    }

    /**
     * 短信验证码登录
     *
     * @param mobile 手机号
     * @param code   验证码
     * @return 访问令牌
     */
    @Override
    public AuthToken loginBySms(String mobile, String code) {
        // 1. 创建用户短信验证码认证的令牌（未认证）
        SmsAuthenticationToken smsAuthenticationToken = new SmsAuthenticationToken(mobile, code);

        // 2. 执行认证（认证中）
        Authentication authentication = authenticationManager.authenticate(smsAuthenticationToken);
        OnlineUser onlineUser = (OnlineUser)  authentication.getPrincipal();
        //SysUser user = sysUserService.findByUsername(username);
        //OnlineUser onlineUser = SysUserConvert.INSTANCE.toOnlineUser(user);
        // 3. 认证成功后生成 JWT 令牌，并存入 Security 上下文，供登录日志 AOP 使用（已认证）
        AuthToken authenticationToken = tokenService.generateToken(onlineUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authenticationToken;
    }

    /**
     * 注销登录
     */
    @Override
    public void logout() {
        String token = SecurityUtils.getTokenFromRequest();
        if (StrUtil.isNotBlank(token) && token.startsWith(SecurityConstants.BEARER_TOKEN_PREFIX)) {
            token = token.substring(SecurityConstants.BEARER_TOKEN_PREFIX.length());
            // 将JWT令牌加入黑名单
            tokenService.invalidateToken(token);
            // 清除Security上下文
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * 获取验证码
     *
     * @return 验证码
     */
    @Override
    public CaptchaInfo getCaptcha() {

        //String captchaType = captchaProperties.getType();
        //int width = captchaProperties.getWidth();
        //int height = captchaProperties.getHeight();
        //int interfereCount = captchaProperties.getInterfereCount();
        //int codeLength = captchaProperties.getCode().getLength();
        //
        //AbstractCaptcha captcha;
        //if (CaptchaTypeEnum.CIRCLE.name().equalsIgnoreCase(captchaType)) {
        //    captcha = CaptchaUtil.createCircleCaptcha(width, height, codeLength, interfereCount);
        //} else if (CaptchaTypeEnum.GIF.name().equalsIgnoreCase(captchaType)) {
        //    captcha = CaptchaUtil.createGifCaptcha(width, height, codeLength);
        //} else if (CaptchaTypeEnum.LINE.name().equalsIgnoreCase(captchaType)) {
        //    captcha = CaptchaUtil.createLineCaptcha(width, height, codeLength, interfereCount);
        //} else if (CaptchaTypeEnum.SHEAR.name().equalsIgnoreCase(captchaType)) {
        //    captcha = CaptchaUtil.createShearCaptcha(width, height, codeLength, interfereCount);
        //} else {
        //    throw new IllegalArgumentException("Invalid captcha type: " + captchaType);
        //}
        //captcha.setGenerator(codeGenerator);
        //captcha.setTextAlpha(captchaProperties.getTextAlpha());
        ////captcha.setFont(captchaFont);
        //
        //String captchaCode = captcha.getCode();
        //String imageBase64Data = captcha.getImageBase64Data();
        //
        //// 验证码文本缓存至Redis，用于登录校验
        //String captchaKey = IdUtil.fastSimpleUUID();
        //redisTemplate.opsForValue().set(
        //        StrUtil.format(RedisConstants.Captcha.IMAGE_CODE, captchaKey),
        //        captchaCode,
        //        captchaProperties.getExpireSeconds(),
        //        TimeUnit.SECONDS
        //);
        //
        //return CaptchaInfo.builder()
        //        .captchaKey(captchaKey)
        //        .captchaBase64(imageBase64Data)
        //        .build();
        return null;
    }

    /**
     * 刷新token
     *
     * @param refreshToken 刷新令牌
     * @return 新的访问令牌
     */
    @Override
    public AuthToken refreshToken(String refreshToken) {
        return tokenService.refreshToken(refreshToken);
    }

    /**
     * 微信小程序Code登录
     *
     * @param loginDTO 登录参数
     * @return 访问令牌
     */
    @Override
    public AuthToken loginByWxMiniAppCode(WxMiniAppCodeLoginDTO loginDTO) {
        // 1. 创建微信小程序认证令牌（未认证）
        WxMiniAppCodeAuthenticationToken authenticationToken = new WxMiniAppCodeAuthenticationToken(loginDTO.getCode());

        // 2. 执行认证（认证中）
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        OnlineUser onlineUser = (OnlineUser)  authentication.getPrincipal();
        //SysUser user = sysUserService.findByUsername(username);
        //OnlineUser onlineUser = SysUserConvert.INSTANCE.toOnlineUser(user);
        // 3. 认证成功后生成 JWT 令牌，并存入 Security 上下文，供登录日志 AOP 使用（已认证）
        AuthToken token = tokenService.generateToken(onlineUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return token;
    }

    /**
     * 微信小程序手机号登录
     *
     * @param loginDTO 登录参数
     * @return 访问令牌
     */
    @Override
    public AuthToken loginByWxMiniAppPhone(WxMiniAppPhoneLoginDTO loginDTO) {
        // 创建微信小程序手机号认证Token
        WxMiniAppPhoneAuthenticationToken authenticationToken = new WxMiniAppPhoneAuthenticationToken(
                loginDTO.getCode(),
                loginDTO.getEncryptedData(),
                loginDTO.getIv()
        );

        // 执行认证
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        OnlineUser onlineUser = (OnlineUser) authentication.getPrincipal();
        //SysUser user = sysUserService.findByUsername(username);
        //OnlineUser onlineUser = SysUserConvert.INSTANCE.toOnlineUser(user);
        // 认证成功后生成JWT令牌，并存入Security上下文
        AuthToken token = tokenService.generateToken(onlineUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return token;
    }

}
