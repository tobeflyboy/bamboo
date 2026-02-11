package com.nutcracker.bamboo.application.service.auth.impl;

import java.util.Collections;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.nutcracker.bamboo.application.service.auth.LoginService;
import com.nutcracker.bamboo.application.service.auth.SysUserService;
import com.nutcracker.bamboo.common.constant.SecurityConstants;
import com.nutcracker.bamboo.common.exception.BusinessException;
import com.nutcracker.bamboo.common.util.JSON;
import com.nutcracker.bamboo.common.wrapper.ResultCode;
import com.nutcracker.bamboo.domain.model.entity.User;
import com.nutcracker.bamboo.domain.model.valueobject.AuthToken;
import com.nutcracker.bamboo.domain.model.valueobject.CaptchaInfo;
import com.nutcracker.bamboo.domain.model.valueobject.OnlineUser;
import com.nutcracker.bamboo.infrastructure.converter.auth.SysUserConvert;
import com.nutcracker.bamboo.web.security.service.TokenService;
import com.nutcracker.bamboo.web.security.util.SecurityUtils;

import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
        User user = sysUserService.findByUsername(username);
        if (null == user) {
            log.error("login error, 账号不存在！username={}", username);
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }
        String pwd = SecurityUtils.encryptPassword(user.getSalt(), password, user.getUsername());
        if (!StrUtil.equals(pwd, user.getPassword())) {
            log.error("login error, 密码错误！username={},password={},pwd={}", username, user.getPassword(), pwd);
            throw new BusinessException(ResultCode.USER_PASSWORD_ERROR);
        }
        // 手动创建已认证的 Authentication
        OnlineUser onlineUser = SysUserConvert.INSTANCE.toOnlineUser(user);
        log.debug("login onlineUser={}", JSON.toJSONString(onlineUser));
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, password, Collections.singletonList(new SimpleGrantedAuthority(user.getRoleCode())));
        // 3. 认证成功后生成 JWT 令牌，并存入 Security 上下文，供登录日志 AOP 使用（已认证）
        AuthToken authenticationTokenResponse = tokenService.generateToken(onlineUser);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        authenticationTokenResponse.setOnlineUser(onlineUser);
        return authenticationTokenResponse;
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



}
