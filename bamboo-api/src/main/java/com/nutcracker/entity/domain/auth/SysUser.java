package com.nutcracker.entity.domain.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nutcracker.common.enums.SysUserStatusEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 用户对象
 *
 * @author 胡桃夹子
 * @date 2025/02/06 09:15:54
 */
@Schema(description = "用户对象")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysUser implements Serializable {

    private static final long serialVersionUID = -2617669111308455616L;

    @Schema(description = "用户id", example = "1909164660242817025")
    private String userId;

    @Schema(description = "账号", example = "vincent")
    private String username;

    @Schema(description = "登录密码", example = "123456")
    private String password;

    @Schema(description = "salt码", name = "salt")
    private String salt;

    @Schema(description = "新登录密码", example = "123456")
    private String newPassword;

    @Schema(description = "姓名", example = "胡桃夹子")
    private String realName;

    @Schema(description = "邮箱", example = "vincent@demo.com")
    private String email;

    @Schema(description = "用户手机", example = "18212344321")
    private String mobile;

    @Schema(description = "微信openid", example = "xaadsfpoiuasdf98")
    private String openid;

    @Schema(description = "状态", example = "1")
    private Integer status;

    @Schema(description = "状态描述", example = "有效")
    private String statusDesc;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    @Schema(description = "创建时间", example = "2025-04-14T13:20:50.987+08:00", type = "string", format = "date-time")
    private LocalDateTime createTime;

    @Schema(description = "创建人", example = "管理员")
    private String createUserRealName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    @Schema(description = "更新时间", example = "2025-04-14T13:20:50.987+08:00")
    private LocalDateTime updateTime;

    @Schema(description = "更新人", example = "管理员")
    private String updateUserRealName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    @Schema(description = "最后登录时间", example = "2025-04-14T13:20:50.987+08:00")
    private LocalDateTime lastLoginTime;

    @Schema(description = "角色id", example = "1909164660242817025")
    private String roleId;

    @Schema(description = "角色编码", example = "admin")
    private String roleCode;

    @Schema(description = "角色", example = "管理员")
    private String roleName;

    /**
     * 获取状态描述
     *
     * @return {@link String }
     */
    public String getStatusDesc() {
        return SysUserStatusEnum.of(this.status);
    }

}
