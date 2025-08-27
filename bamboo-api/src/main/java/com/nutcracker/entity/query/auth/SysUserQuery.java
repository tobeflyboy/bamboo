package com.nutcracker.entity.query.auth;

import com.nutcracker.entity.query.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;

/**
 * sys用户查询
 *
 * @author 胡桃夹子
 * @date 2025/08/19 17:13:03
 */
@Schema(description = "用户查询")
@Setter
@Getter
@ToString(callSuper = true)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SysUserQuery extends BasePageQuery {

    @Serial
    private static final long serialVersionUID = -2147176399743379427L;

    @Schema(description = "用户ID", example = "1")
    private String userId;

    @Schema(description = "账号", example = "vincent")
    private String username;

    @Schema(description = "姓名", example = "胡桃夹子")
    private String realName;

    @Schema(description = "状态", example = "1")
    private Integer status;

}
