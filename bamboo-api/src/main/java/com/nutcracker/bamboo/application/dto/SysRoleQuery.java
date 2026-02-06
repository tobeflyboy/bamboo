package com.nutcracker.bamboo.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 角色分页查询对象
 *
 * @author 胡桃夹子
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "角色分页查询对象")
public class SysRoleQuery extends BasePageQuery{

    @Schema(description = "角色编码，精准查询", example = "admin")
    private String roleCode;

    @Schema(description = "角色名称，模糊查询", example = "管理员")
    private String roleName;

    @Min(value = 1, message = "页码必须大于0")
    @Override
    public int getPageNum() {
        return super.getPageNum();
    }

    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100")
    @Override
    public int getPageSize() {
        return super.getPageSize();
    }
}
