package com.nutcracker.entity.query.auth;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.nutcracker.entity.query.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.time.LocalDateTime;

/**
 * role query entity
 *
 * @author 胡桃夹子
 * @since 2025-09-17 14:42
 */
@Schema(description = "角色查询")
@Setter
@Getter
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysRoleQuery extends BasePageQuery {

    @Serial
    private static final long serialVersionUID = -3400097996428330935L;

    @Schema(description = "角色id", requiredMode = Schema.RequiredMode.REQUIRED)
    private String id;

    @Schema(description = "角色编码", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleCode;

    @Schema(description = "角色名称", requiredMode = Schema.RequiredMode.REQUIRED)
    private String roleName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS", timezone = "GMT+8")
    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED, example = "2025-04-14T13:20:50.987+08:00", type = "string", format = "date-time")
    private LocalDateTime createTime;

    @Schema(description = "创建人")
    private String createUserRealName;
}
