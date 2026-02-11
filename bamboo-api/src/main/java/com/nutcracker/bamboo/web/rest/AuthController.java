package com.nutcracker.bamboo.web.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nutcracker.bamboo.application.model.response.RouteRecordRawVo;
import com.nutcracker.bamboo.application.service.auth.LoginService;
import com.nutcracker.bamboo.application.service.auth.PermissionService;
import com.nutcracker.bamboo.common.util.JSON;
import com.nutcracker.bamboo.common.wrapper.ResultCode;
import com.nutcracker.bamboo.common.wrapper.WrapperResp;
import com.nutcracker.bamboo.domain.model.valueobject.AuthToken;
import com.nutcracker.bamboo.domain.model.valueobject.CaptchaInfo;
import com.nutcracker.bamboo.domain.model.valueobject.OnlineUser;
import com.nutcracker.bamboo.web.Identify;
import com.nutcracker.bamboo.web.security.util.SecurityUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


/**
 * 认证控制层
 *
 * @author 胡桃夹子
 * @since 2022/10/16
 */
@Tag(name = "1.认证中心")
@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final LoginService loginService;
    private final PermissionService sysPermissionService;

    @Operation(summary = "获取验证码")
    @GetMapping("/api/auth/captcha")
    public WrapperResp<CaptchaInfo> getCaptcha() {
        CaptchaInfo captcha = loginService.getCaptcha();
        return WrapperResp.success(captcha);
    }

    @Operation(summary = "以账号密码登录")
    @PostMapping("/api/auth/login")
    public WrapperResp<AuthToken> authLogin(
            @Parameter(description = "用户名", example = "admin") @RequestParam String username,
            @Parameter(description = "密码", example = "123456") @RequestParam String password
    ) {
        log.debug("username={},password={}", username, password);
        AuthToken authenticationToken = loginService.login(username, password);
        return WrapperResp.success(authenticationToken);
    }




    @Operation(summary = "退出登录")
    @PostMapping("/api/auth/logout")
    public WrapperResp<?> logout() {
        loginService.logout();
        return WrapperResp.success();
    }

    @Operation(summary = "刷新令牌")
    @PostMapping("/api/auth/refresh-token")
    public WrapperResp<?> refreshToken(
            @Parameter(description = "刷新令牌", example = "xxx.xxx.xxx") @RequestParam String refreshToken
    ) {
        AuthToken authenticationToken = loginService.refreshToken(refreshToken);
        return WrapperResp.success(authenticationToken);
    }

    @Operation(summary = "用户信息", description = "获取用户信息")
    @PostMapping("/api/auth/userInfo")
    public WrapperResp<OnlineUser> userInfo() {
        OnlineUser onlineUser = SecurityUtils.getUser().orElse(null);
        WrapperResp<OnlineUser> resp;
        if (null != onlineUser) {
            resp = WrapperResp.success(onlineUser);
        } else {
            resp = WrapperResp.failed(ResultCode.ACCESS_TOKEN_INVALID);
        }
        log.info("/userInfo,{}", JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "用户菜单权限数据", description = "获取用户菜单权限数据")
    @GetMapping("/api/auth/userMenus")
    public WrapperResp<List<RouteRecordRawVo>> userMenus() {
        // 优先使用ThreadLocal获取用户信息
        OnlineUser onlineUser = Identify.getSessionUser();
        if (onlineUser == null) {
            // fallback到SecurityContext
            onlineUser = SecurityUtils.getUser().orElse(null);
        }
        if (onlineUser == null) {
            log.warn("/api/auth/userMenus - 无法获取当前用户信息");
            return WrapperResp.failed(ResultCode.ACCESS_TOKEN_INVALID);
        }
        log.debug("==> /api/userMenus begin, roleId={}, userId={}", onlineUser.getRoleId(), onlineUser.getUserId());
        List<RouteRecordRawVo> permissions = sysPermissionService.getRolePermissionByRoleId(onlineUser.getRoleId());
        WrapperResp<List<RouteRecordRawVo>> resp;
        if (null != permissions) {
            resp = WrapperResp.success(permissions);
        } else {
            resp = WrapperResp.failed(ResultCode.ACCESS_UNAUTHORIZED);
        }
        log.debug("<== /api/userMenus end, roleId={},resp={}\n", onlineUser.getRoleId(), JSON.toJSONString(resp));
        return resp;
    }

}
