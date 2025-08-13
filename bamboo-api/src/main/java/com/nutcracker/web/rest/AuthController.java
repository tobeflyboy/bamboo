package com.nutcracker.web.rest;

import com.nutcracker.common.wrapper.ResultCode;
import com.nutcracker.common.wrapper.WrapperResp;
import com.nutcracker.entity.domain.auth.AuthToken;
import com.nutcracker.entity.domain.auth.CaptchaInfo;
import com.nutcracker.entity.domain.auth.OnlineUser;
import com.nutcracker.entity.dto.WxMiniAppCodeLoginDTO;
import com.nutcracker.entity.dto.WxMiniAppPhoneLoginDTO;
import com.nutcracker.entity.vo.auth.RouteRecordRawVo;
import com.nutcracker.service.auth.LoginService;
import com.nutcracker.service.auth.SysPermissionService;
import com.nutcracker.util.JSON;
import com.nutcracker.web.Identify;
import com.nutcracker.web.security.util.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


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
    private final SysPermissionService sysPermissionService;

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

    @Operation(summary = "短信验证码登录")
    @PostMapping("/api/auth/login/sms")
    public WrapperResp<AuthToken> loginBySms(
            @Parameter(description = "手机号", example = "18812345678") @RequestParam String mobile,
            @Parameter(description = "验证码", example = "1234") @RequestParam String code
    ) {
        AuthToken loginResult = loginService.loginBySms(mobile, code);
        return WrapperResp.success(loginResult);
    }

    @Operation(summary = "发送登录短信验证码")
    @PostMapping("/api/auth/sms/code")
    public WrapperResp<Void> sendLoginVerifyCode(
            @Parameter(description = "手机号", example = "18812345678") @RequestParam String mobile
    ) {
        loginService.sendSmsLoginCode(mobile);
        return WrapperResp.success();
    }

    @Operation(summary = "微信授权登录(Web)")
    @PostMapping("/api/auth/login/wechat")
    public WrapperResp<AuthToken> loginByWechat(
            @Parameter(description = "微信授权码", example = "code") @RequestParam String code
    ) {
        AuthToken loginResult = loginService.loginByWechat(code);
        return WrapperResp.success(loginResult);
    }

    @Operation(summary = "微信小程序登录(Code)")
    @PostMapping("/api/auth/wx/miniapp/code-login")
    public WrapperResp<AuthToken> loginByWxMiniAppCode(@RequestBody @Valid WxMiniAppCodeLoginDTO loginDTO) {
        AuthToken token = loginService.loginByWxMiniAppCode(loginDTO);
        return WrapperResp.success(token);
    }

    @Operation(summary = "微信小程序登录(手机号)")
    @PostMapping("/api/auth/wx/miniapp/phone-login")
    public WrapperResp<AuthToken> loginByWxMiniAppPhone(@RequestBody @Valid WxMiniAppPhoneLoginDTO loginDTO) {
        AuthToken token = loginService.loginByWxMiniAppPhone(loginDTO);
        return WrapperResp.success(token);
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
        OnlineUser onlineUser = Identify.getSessionUser();
        log.debug("==> /api/userMenus begin, roleId={}", onlineUser.getRoleId());
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
