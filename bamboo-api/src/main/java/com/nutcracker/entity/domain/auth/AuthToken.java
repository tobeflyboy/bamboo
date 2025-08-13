package com.nutcracker.entity.domain.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 登录用户vo
 *
 * @author 胡桃夹子
 * @date 2024-04-11 17:35
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthToken implements Serializable {

    private static final long serialVersionUID = 2573965649742628481L;

    @Schema(description = "token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImlkIjoxLCJ1c2VybmFtZSI6IuW8oOS4iSJ9LCJleHAiOjE3NDQyNjYwMjF9.KNDyfjv5zxe_oetmNvRpwIDVJFNM_Oyf5sVG9p9KrUQ")
    private String token;

    @Schema(description = "refreshToken", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyIjp7ImlkIjoxLCJ1c2VybmFtZSI6IuW8oOS4iSJ9LCJleHAiOjE3NDQyNjYwMjF9.KNDyfjv5zxe_oetmNvRpwIDVJFNM_Oyf5sVG9p9KrUQ")
    private String refreshToken;

    @Schema(description = "会话过期时间", example = "182827785478483968")
    private LocalDateTime expiresAt;

    @Schema(description = "当前登录用户对象", example = "test")
    private OnlineUser onlineUser;
}
