package com.nutcracker.bamboo.domain.model.entity;

import java.io.Serial;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户角色对象
 *
 * @author 胡桃夹子
 * @date 2025/02/06 09:15:54
 */

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRole implements Serializable {

    @Serial
    private static final long serialVersionUID = -7686199439208183608L;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 账号
     */
    private String username;

    /**
     * 姓名
     */
    private String realName;

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

}
