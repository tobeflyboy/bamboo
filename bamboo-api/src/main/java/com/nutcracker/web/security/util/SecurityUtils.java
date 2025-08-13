package com.nutcracker.web.security.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.nutcracker.common.exception.BusinessException;
import com.nutcracker.constant.SecurityConstants;
import com.nutcracker.constant.SystemConstants;
import com.nutcracker.entity.domain.auth.OnlineUser;
import com.nutcracker.util.JSON;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Spring Security 工具类
 *
 * @author 胡桃夹子
 * @since 2021/1/10
 */
@Slf4j
public class SecurityUtils {


    // 默认的哈希算法名称
    private static final String ALGORITHM_NAME = "SHA-256";
    // 默认的哈希迭代次数
    private static final int HASH_ITERATIONS = 2;

    // 用于生成随机字符串的字符集
    private static final String ALPHABET_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    // 安全的随机数生成器
    private static final SecureRandom RANDOM = new SecureRandom();


    /**
     * 生成10个长度的随机字母字符串。
     *
     * @return 随机生成的字母字符串
     */
    public static String randomAlphabetic() {
        return randomAlphabetic(10);
    }

    /**
     * 生成指定长度的随机字母字符串。
     *
     * @param length 字符串的长度
     * @return 随机生成的字母字符串
     */
    public static String randomAlphabetic(int length) {
        if (length < 0) {
            throw new IllegalArgumentException("长度不能为负");
        }
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = RANDOM.nextInt(ALPHABET_CHARS.length());
            builder.append(ALPHABET_CHARS.charAt(index));
        }
        return builder.toString();
    }

    /**
     * 将字节数组转换为十六进制字符串。
     *
     * @param hash 哈希字节数组
     * @return 十六进制字符串
     */
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 加密密码。
     *
     * @param salt     盐值
     * @param pwd      明文密码
     * @param userName 用户名
     * @return 加密后的密码
     */
    public static String encryptPassword(String salt, String pwd, String userName) {
        try {
            // 使用用户 + 盐值生成盐字节
            byte[] saltBytes = (userName + salt).getBytes(StandardCharsets.UTF_8);
            byte[] hash = pwd.getBytes(StandardCharsets.UTF_8);

            // 初始化 MessageDigest
            MessageDigest md = MessageDigest.getInstance(ALGORITHM_NAME);
            md.update(saltBytes);
            hash = md.digest(hash);

            // 执行多次迭代
            for (int i = 1; i < HASH_ITERATIONS; i++) {
                // MessageDigest 会自动重置
                hash = md.digest(hash);
            }

            return bytesToHex(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new BusinessException("加密算法未找到", e);
        }
    }

    /**
     * 获取当前登录人信息
     *
     * @return Optional<SysUserDetails>
     */
    public static Optional<OnlineUser> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Object principal = authentication.getPrincipal();
            log.debug("principal={}", JSON.toJSONString(principal));
            if (principal instanceof OnlineUser) {
                return Optional.of((OnlineUser) principal);
            }
        }
        return Optional.empty();
    }


    /**
     * 获取用户ID
     *
     * @return Long
     */
    public static String getUserId() {
        return getUser().map(OnlineUser::getUserId).orElse(null);
    }


    /**
     * 获取用户账号
     *
     * @return String 用户账号
     */
    public static String getUsername() {
        return getUser().map(OnlineUser::getUsername).orElse(null);
    }

    /**
     * 获取角色集合
     *
     * @return 角色集合
     */
    public static Set<String> getRoles() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .map(Authentication::getAuthorities)
                .filter(CollectionUtil::isNotEmpty)
                .stream()
                .flatMap(Collection::stream)
                .map(GrantedAuthority::getAuthority)
                // 筛选角色,authorities 中的角色都是以 ROLE_ 开头
                .filter(authority -> authority.startsWith(SecurityConstants.ROLE_PREFIX))
                .map(authority -> StrUtil.removePrefix(authority, SecurityConstants.ROLE_PREFIX))
                .collect(Collectors.toSet());
    }

    /**
     * 是否超级管理员
     * <p>
     * 超级管理员忽视任何权限判断
     */
    public static boolean isRoot() {
        Set<String> roles = getRoles();
        return roles.contains(SystemConstants.ROOT_ROLE_CODE);
    }

    /**
     * 获取请求中的 Token
     *
     * @return Token 字符串
     */
    public static String getTokenFromRequest() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return request.getHeader(HttpHeaders.AUTHORIZATION);
    }


}
