package com.nutcracker.bamboo.application.model.query;

import java.io.Serial;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 用户分页查询对象
 *
 * @author 胡桃夹子
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Schema(description = "用户分页查询对象")
public class UserQuery extends BasePageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "关键词(用户名/手机号/姓名)", example = "admin")
    private String keywords;

    @Schema(description = "用户名，精准查询", example = "admin")
    private String username;

    @Schema(description = "真实姓名，模糊查询", example = "张三")
    private String realName;

    @Schema(description = "用户状态(0-禁用,1-正常)", example = "1")
    private Integer status;

    @Schema(description = "用户ID", example = "1")
    private String userId;

    @Schema(description = "角色ID", example = "1")
    private String roleId;

    @Schema(description = "部门ID", example = "1")
    private String deptId;

}
