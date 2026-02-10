package com.nutcracker.bamboo.domain.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.nutcracker.bamboo.common.enums.SysUserStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户对象
 *
 * @author 胡桃夹子
 * @date 2025/02/06 09:15:54
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements Serializable {

    @Serial
    private static final long serialVersionUID = -2617669111308455616L;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 账号
     */
    private String username;

    /**
     * 登录密码
     */
    private String password;

    /**
     * salt码
     */
    private String salt;

    /**
     * 新登录密码
     */
    private String newPassword;

    /**
     * 姓名
     */
    private String realName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 用户手机
     */
    private String mobile;

    /**
     * 微信openid
     */
    private String openid;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 状态描述
     */
    private String statusDesc;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createUserRealName;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 更新人
     */
    private String updateUserRealName;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色
     */
    private String roleName;

    /**
     * 获取状态描述
     *
     * @return {@link String }
     */
    public String getStatusDesc() {
        return SysUserStatusEnum.of(this.status);
    }
    
    // 手动添加getter方法以解决编译问题
    public String getPassword() {
        return this.password;
    }
    
    public String getRoleCode() {
        return this.roleCode;
    }

}
