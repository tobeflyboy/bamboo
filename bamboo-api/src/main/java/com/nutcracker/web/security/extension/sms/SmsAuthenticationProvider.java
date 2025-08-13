package com.nutcracker.web.security.extension.sms;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.nutcracker.common.exception.BusinessException;
import com.nutcracker.common.wrapper.ResultCode;
import com.nutcracker.constant.RedisConstants;
import com.nutcracker.entity.convert.auth.SysUserConvert;
import com.nutcracker.entity.domain.auth.OnlineUser;
import com.nutcracker.entity.domain.auth.SysUser;
import com.nutcracker.service.auth.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Collections;


/**
 * 短信验证码认证 Provider
 *
 * @author 胡桃夹子
 */
@Slf4j
public class SmsAuthenticationProvider implements AuthenticationProvider {

    private final SysUserService sysUserService;
    private final RedisTemplate<String, Object> redisTemplate;

    public SmsAuthenticationProvider(SysUserService sysUserService, RedisTemplate<String, Object> redisTemplate) {
        this.sysUserService = sysUserService;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 短信验证码认证逻辑，参考 Spring Security 认证密码校验流程
     *
     * @param authentication 认证对象
     * @return 认证后的 Authentication 对象
     * @throws AuthenticationException 认证异常
     * @see org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider#authenticate(Authentication)
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String mobile = (String) authentication.getPrincipal();
        String inputVerifyCode = (String) authentication.getCredentials();

        // 根据手机号获取用户信息
        SysUser user = sysUserService.findByMobile(mobile);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        // 检查用户状态是否有效
        if (ObjectUtil.notEqual(user.getStatus(), 1)) {
            throw new DisabledException("用户已被禁用");
        }

        // 校验发送短信验证码的手机号是否与当前登录用户一致
        String cacheKey = StrUtil.format(RedisConstants.Captcha.SMS_LOGIN_CODE, mobile);
        String cachedVerifyCode = (String) redisTemplate.opsForValue().get(cacheKey);

        if (!StrUtil.equals(inputVerifyCode, cachedVerifyCode)) {
            throw new BusinessException(ResultCode.USER_VERIFICATION_CODE_ERROR);
        } else {
            // 验证成功后删除验证码
            redisTemplate.delete(cacheKey);
        }
        OnlineUser onlineUser = SysUserConvert.INSTANCE.toOnlineUser(user);
        // 创建已认证的 SmsAuthenticationToken
        return SmsAuthenticationToken.authenticated(onlineUser, Collections.singletonList(new SimpleGrantedAuthority(onlineUser.getRoleCode())));
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return SmsAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
