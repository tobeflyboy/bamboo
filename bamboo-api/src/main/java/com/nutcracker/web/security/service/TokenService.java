package com.nutcracker.web.security.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTPayload;
import cn.hutool.jwt.JWTUtil;
import com.nutcracker.common.exception.BusinessException;
import com.nutcracker.common.wrapper.ResultCode;
import com.nutcracker.config.property.SecurityProperties;
import com.nutcracker.constant.JwtClaimConstants;
import com.nutcracker.constant.RedisConstants;
import com.nutcracker.entity.domain.auth.AuthToken;
import com.nutcracker.entity.domain.auth.OnlineUser;
import com.nutcracker.util.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis Token 管理器
 * <p>
 * 用于生成、解析、校验、刷新 JWT Token
 *
 * @author 胡桃夹子
 */
@Slf4j
@Service
public class TokenService {

    private final SecurityProperties securityProperties;
    private final RedisTemplate<String, Object> redisTemplate;
    private final byte[] secretKey;

    public TokenService(SecurityProperties securityProperties, RedisTemplate<String, Object> redisTemplate) {
        this.securityProperties = securityProperties;
        this.redisTemplate = redisTemplate;
        log.debug("secretkey={}", securityProperties.getSession().getSecretKey());
        this.secretKey = securityProperties.getSession().getSecretKey().getBytes();
    }


    /**
     * 生成 Token
     *
     * @return 生成的 AuthenticationToken 对象
     */
    public AuthToken generateToken(OnlineUser onlineUser) {
        int accessTokenTimeToLive = securityProperties.getSession().getAccessTokenTimeToLive();
        int refreshTokenTimeToLive = securityProperties.getSession().getRefreshTokenTimeToLive();

        String accessToken = generateToken(onlineUser, accessTokenTimeToLive);
        String refreshToken = generateToken(onlineUser, refreshTokenTimeToLive);

        // 存储访问令牌、刷新令牌和刷新令牌映射
        storeTokensInRedis(accessToken, refreshToken, onlineUser);

        // 单设备登录控制
        handleSingleDeviceLogin(onlineUser.getUserId(), accessToken);
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(securityProperties.getSession().getAccessTokenTimeToLive());
        return AuthToken.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .onlineUser(onlineUser)
                .build();
    }

    /**
     * 生成 JWT Token
     *
     * @param ttl 过期时间
     * @return JWT Token
     */
    private String generateToken(OnlineUser onlineUser, int ttl) {
        Date now = new Date();
        Map<String, Object> payload = new HashMap<>();
        payload.put(JwtClaimConstants.USER_ID, onlineUser.getUserId());
        payload.put(JwtClaimConstants.USERNAME, onlineUser.getUsername());
        payload.put(JwtClaimConstants.REAL_NAME, onlineUser.getRealName());
        payload.put(JwtClaimConstants.STATUS, onlineUser.getStatus());
        payload.put(JwtClaimConstants.ROLE_ID, onlineUser.getRoleId());
        payload.put(JwtClaimConstants.ROLE_CODE, onlineUser.getRoleCode());
        payload.put(JwtClaimConstants.ROLE_NAME, onlineUser.getRoleName());
        payload.put(JWTPayload.ISSUED_AT, now);
        // 设置过期时间 -1 表示永不过期
        if (ttl != -1) {
            Date expiresAt = DateUtil.offsetSecond(now, ttl);
            payload.put(JWTPayload.EXPIRES_AT, expiresAt);
        }
        payload.put(JWTPayload.SUBJECT, onlineUser.getUsername());
        payload.put(JWTPayload.JWT_ID, IdUtil.simpleUUID());
        log.info("generateToken payload={}", JSON.toJSONString(payload));
        return JWTUtil.createToken(payload, secretKey);
    }

    /**
     * 根据 token 解析用户信息
     *
     * @param token JWT Token
     * @return 构建的 Authentication 对象
     */
    public Authentication parseToken(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        JSONObject payloads = jwt.getPayloads();
        log.info("payloads={}", payloads);
        if (payloads == null) {
            return null;
        }
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(formatTokenKey(token));
        log.debug("onlineUser={},token={}", JSON.toJSONString(onlineUser), token);
        Collection<SimpleGrantedAuthority> collection = onlineUser != null ? List.of(new SimpleGrantedAuthority(onlineUser.getRoleCode())) : null;
        return new UsernamePasswordAuthenticationToken(onlineUser, null, collection);
    }

    /**
     * 校验 Token 是否有效
     *
     * @param token  访问令牌
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        return validateToken(token, false);
    }

    /**
     * 校验 RefreshToken 是否有效
     *
     * @param refreshToken  访问令牌
     * @return 是否有效
     */
    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, true);
    }

    /**
     * 校验令牌
     *
     * @param token                JWT Token
     * @param validateRefreshToken 是否校验刷新令牌
     * @return 是否有效
     */
    private boolean validateToken(String token, boolean validateRefreshToken) {
        if (StrUtil.isBlank(token)) {
            log.warn("validateToken token is blank.");
            return false;
        }
        JWT jwt = JWTUtil.parseToken(token);
        try {
            // 验签（使用相同的密钥）
            boolean verify = jwt.setKey(secretKey).verify();
            if (!verify) {
                log.warn("validateToken, verify is false, token={}", token);
                return false;
            }
            // 检查是否过期，0 表示允许的时钟偏移（秒）
            boolean isNotExpire = jwt.validate(0);
            if (!isNotExpire) {
                log.warn("validateToken, isNotExpire is false, token={}", token);
                return false;
            }

            // 判断是否在黑名单中，如果在，则返回 false 标识Token无效
            // 检查 Token 是否已被加入黑名单(注销、修改密码等场景)
            JSONObject payloads = jwt.getPayloads();
            String jti = payloads.getStr(JWTPayload.JWT_ID);
            if (redisTemplate.hasKey(StrUtil.format(RedisConstants.Auth.BLACKLIST_TOKEN, jti))) {
                log.warn("validateToken,token is blacklist, token={}", token);
                return false;
            }

            boolean validate;
            if (validateRefreshToken) {
                validate = redisTemplate.hasKey(formatRefreshTokenKey(token));
            } else {
                validate = redisTemplate.hasKey(formatTokenKey(token));
            }
            log.debug("validateToken validate={},validateRefreshToken={},token={}", validate, validateRefreshToken, token);
            return validate;
        } catch (Exception e) {
            log.warn("validateToken fail, token={}", token, e);
            return false;
        }
    }

    /**
     * 刷新令牌
     *
     * @param refreshToken 刷新令牌
     * @return 新生成的 AuthenticationToken 对象
     */
    public AuthToken refreshToken(String refreshToken) {
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken));
        if (onlineUser == null) {
            throw new BusinessException(ResultCode.REFRESH_TOKEN_INVALID);
        }

        String oldToken = (String) redisTemplate.opsForValue().get(StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, onlineUser.getUserId()));

        // 删除旧的访问令牌记录
        if (oldToken != null) {
            redisTemplate.delete(formatTokenKey(oldToken));
        }

        // 生成新访问令牌并存储
        String newToken = IdUtil.fastSimpleUUID();
        storeAccessToken(newToken, onlineUser);

        int accessTtl = securityProperties.getSession().getAccessTokenTimeToLive();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(accessTtl);
        return AuthToken.builder()
                .token(newToken)
                .refreshToken(refreshToken)
                .expiresAt(expiresAt)
                .onlineUser(onlineUser)
                .build();
    }

    /**
     * 使访问令牌失效
     *
     * @param token 访问令牌
     */
    public void invalidateToken(String token) {
        OnlineUser onlineUser = (OnlineUser) redisTemplate.opsForValue().get(formatTokenKey(token));
        if (onlineUser != null) {
            String userId = onlineUser.getUserId();
            // 1. 删除访问令牌相关
            String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, userId);
            String accessToken = (String) redisTemplate.opsForValue().get(userAccessKey);
            if (accessToken != null) {
                redisTemplate.delete(formatTokenKey(accessToken));
                redisTemplate.delete(userAccessKey);
            }

            // 2. 删除刷新令牌相关
            String userRefreshKey = StrUtil.format(RedisConstants.Auth.USER_REFRESH_TOKEN, userId);
            String refreshToken = (String) redisTemplate.opsForValue().get(userRefreshKey);
            if (refreshToken != null) {
                redisTemplate.delete(StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken));
                redisTemplate.delete(userRefreshKey);
            }
        }
    }

    /**
     * 将访问令牌和刷新令牌存储至 Redis
     *
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     * @param onlineUser 在线用户信息
     */
    private void storeTokensInRedis(String accessToken, String refreshToken, OnlineUser onlineUser) {
        int accessTokenTimeToLive = securityProperties.getSession().getAccessTokenTimeToLive();
        int refreshTokenTimeToLive = securityProperties.getSession().getRefreshTokenTimeToLive();
        // 访问令牌 -> 用户信息
        setRedisValue(formatTokenKey(accessToken), onlineUser, accessTokenTimeToLive);

        // 刷新令牌 -> 用户信息
        String refreshTokenKey = StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken);
        setRedisValue(refreshTokenKey, onlineUser, refreshTokenTimeToLive);

        // 用户ID -> 刷新令牌
        setRedisValue(StrUtil.format(RedisConstants.Auth.USER_REFRESH_TOKEN, onlineUser.getUserId()), refreshToken, refreshTokenTimeToLive);
    }

    /**
     * 处理单设备登录控制
     *
     * @param userId 用户ID
     * @param accessToken 新生成的访问令牌
     */
    private void handleSingleDeviceLogin(String userId, String accessToken) {
        Boolean allowMultiLogin = securityProperties.getSession().getAllowMultiLogin();
        String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, userId);
        // 单设备登录控制，删除旧的访问令牌
        if (!allowMultiLogin) {
            String oldAccessToken = (String) redisTemplate.opsForValue().get(userAccessKey);
            if (oldAccessToken != null) {
                redisTemplate.delete(formatTokenKey(oldAccessToken));
            }
        }
        // 存储访问令牌映射（用户ID -> 访问令牌），用于单设备登录控制删除旧的访问令牌和刷新令牌时删除旧令牌
        setRedisValue(userAccessKey, accessToken, securityProperties.getSession().getAccessTokenTimeToLive());
    }

    /**
     * 存储新的访问令牌
     *
     * @param newAccessToken 新访问令牌
     * @param onlineUser 在线用户信息
     */
    private void storeAccessToken(String newAccessToken, OnlineUser onlineUser) {
        int accessTokenTimeToLive = securityProperties.getSession().getAccessTokenTimeToLive();
        setRedisValue(StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, newAccessToken), onlineUser, accessTokenTimeToLive);
        String userAccessKey = StrUtil.format(RedisConstants.Auth.USER_ACCESS_TOKEN, onlineUser.getUserId());
        setRedisValue(userAccessKey, newAccessToken, accessTokenTimeToLive);
    }


    /**
     * 格式化访问令牌的 Redis 键
     *
     * @param token 访问令牌
     * @return 格式化后的 Redis 键
     */
    private String formatTokenKey(String token) {
        return StrUtil.format(RedisConstants.Auth.ACCESS_TOKEN_USER, token);
    }

    /**
     * 格式化刷新令牌的 Redis 键
     *
     * @param refreshToken 访问令牌
     * @return 格式化后的 Redis 键
     */
    private String formatRefreshTokenKey(String refreshToken) {
        return StrUtil.format(RedisConstants.Auth.REFRESH_TOKEN_USER, refreshToken);
    }

    /**
     * 将值存储到 Redis
     *
     * @param key   键
     * @param value 值
     * @param ttl   过期时间（秒），-1表示永不过期
     */
    private void setRedisValue(String key, Object value, int ttl) {
        if (ttl != -1) {
            redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
        } else {
            // ttl=-1时永不过期
            redisTemplate.opsForValue().set(key, value);
        }
    }
}
