package com.nutcracker.bamboo.domain.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * sys_role domain
 *
 * @author 胡桃夹子
 * @date 2025/01/02 14:25:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Role implements Serializable {

    @Serial
    private static final long serialVersionUID = -6982490361440305761L;

    /**
     * 角色id
     */
    private String id;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 创建人
     */
    private String createUserRealName;

}
