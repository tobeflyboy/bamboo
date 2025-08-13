package com.nutcracker.entity.domain.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统配置视图对象
 *
 * @author 胡桃夹子
 */
@Data
@Builder
@EqualsAndHashCode(callSuper = false)
@Schema(description = "系统配置VO")
public class SysConfig {

    @Schema(description = "主键")
    private String id;

    @Schema(description = "配置名称")
    private String configName;

    @Schema(description = "配置键")
    private String configKey;

    @Schema(description = "配置值")
    private String configValue;

    @Schema(description = "描述、备注")
    private String remark;
}
