package com.nutcracker.config;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.hutool.core.util.ArrayUtil;
import com.nutcracker.config.property.SecurityProperties;
import com.nutcracker.service.auth.SysConfigService;
import com.nutcracker.service.auth.SysUserService;
import com.nutcracker.web.filter.RateLimiterFilter;
import com.nutcracker.web.security.exception.MyAccessDeniedHandler;
import com.nutcracker.web.security.exception.MyAuthenticationEntryPoint;
import com.nutcracker.web.security.extension.sms.SmsAuthenticationProvider;
import com.nutcracker.web.security.extension.wx.WxMiniAppCodeAuthenticationProvider;
import com.nutcracker.web.security.extension.wx.WxMiniAppPhoneAuthenticationProvider;
import com.nutcracker.web.security.filter.TokenAuthenticationFilter;
import com.nutcracker.web.security.service.SysUserDetailsService;
import com.nutcracker.web.security.service.TokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 配置类
 *
 * @author 胡桃夹子
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final RedisTemplate<String, Object> redisTemplate;
    //private final PasswordEncoder passwordEncoder;

    private final TokenService tokenService;
    private final WxMaService wxMaService;
    private final SysUserService sysUserService;
    private final SysUserDetailsService userDetailsService;

    //private final CodeGenerator codeGenerator;
    private final SysConfigService sysConfigService;
    private final SecurityProperties securityProperties;

    /**
     * 配置安全过滤链 SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .authorizeHttpRequests(requestMatcherRegistry -> {
                            // 配置无需登录即可访问的公开接口
                            String[] ignoreUrls = securityProperties.getIgnoreUrls();
                            if (ArrayUtil.isNotEmpty(ignoreUrls)) {
                                requestMatcherRegistry.requestMatchers(ignoreUrls).permitAll();
                            }
                            // 其他所有请求需登录后访问
                            requestMatcherRegistry.anyRequest().authenticated();
                        }
                )
                .exceptionHandling(configurer ->
                        configurer
                                // 未认证异常处理器
                                .authenticationEntryPoint(new MyAuthenticationEntryPoint())
                                // 无权限访问异常处理器
                                .accessDeniedHandler(new MyAccessDeniedHandler())
                )

                // 禁用默认的 Spring Security 特性，适用于前后端分离架构
                .sessionManagement(configurer ->
                        // 无状态认证，不使用 Session
                        configurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // 禁用 CSRF 防护，前后端分离无需此防护机制
                .csrf(AbstractHttpConfigurer::disable)
                // 禁用默认的表单登录功能，前后端分离采用 Token 认证方式
                .formLogin(AbstractHttpConfigurer::disable)
                // 禁用 HTTP Basic 认证，避免弹窗式登录
                .httpBasic(AbstractHttpConfigurer::disable)
                // 禁用 X-Frame-Options 响应头，允许页面被嵌套到 iframe 中
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                // 限流过滤器
                .addFilterBefore(new RateLimiterFilter(redisTemplate, sysConfigService), UsernamePasswordAuthenticationFilter.class)
                // 验证码校验过滤器
                //.addFilterBefore(new CaptchaValidationFilter(redisTemplate, codeGenerator), UsernamePasswordAuthenticationFilter.class)
                // 验证和解析过滤器
                .addFilterBefore(new TokenAuthenticationFilter(tokenService), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    /**
     * 配置Web安全自定义器，以忽略特定请求路径的安全性检查。
     * <p>
     * 该配置用于指定哪些请求路径不经过Spring Security过滤器链。通常用于静态资源文件。
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> {
            String[] unsecuredUrls = securityProperties.getUnsecuredUrls();
            if (ArrayUtil.isNotEmpty(unsecuredUrls)) {
                web.ignoring().requestMatchers(unsecuredUrls);
            }
        };
    }

    // ✅ 新增：提供一个 NoOpPasswordEncoder（占位用）
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        // 或者直接返回 NoOp（仅测试用）
        // return NoOpPasswordEncoder.getInstance();
    }


    /**
     * 默认密码认证的 Provider
     */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    /**
     * 微信小程序Code认证Provider
     */
    @Bean
    public WxMiniAppCodeAuthenticationProvider wxMiniAppCodeAuthenticationProvider() {
        return new WxMiniAppCodeAuthenticationProvider(sysUserService, wxMaService);
    }

    /**
     * 微信小程序手机号认证Provider
     */
    @Bean
    public WxMiniAppPhoneAuthenticationProvider wxMiniAppPhoneAuthenticationProvider() {
        return new WxMiniAppPhoneAuthenticationProvider(sysUserService, wxMaService);
    }

    /**
     * 短信验证码认证 Provider
     */
    @Bean
    public SmsAuthenticationProvider smsAuthenticationProvider() {
        return new SmsAuthenticationProvider(sysUserService, redisTemplate);
    }

    /**
     * 认证管理器
     */
    @Bean
    public AuthenticationManager authenticationManager(
            DaoAuthenticationProvider daoAuthenticationProvider,
            WxMiniAppCodeAuthenticationProvider wxMiniAppCodeAuthenticationProvider,
            WxMiniAppPhoneAuthenticationProvider wxMiniAppPhoneAuthenticationProvider,
            SmsAuthenticationProvider smsAuthenticationProvider
    ) {
        return new ProviderManager(
                daoAuthenticationProvider,
                wxMiniAppCodeAuthenticationProvider,
                wxMiniAppPhoneAuthenticationProvider,
                smsAuthenticationProvider
        );
    }
}
