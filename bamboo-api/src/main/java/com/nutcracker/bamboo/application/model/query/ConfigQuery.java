package com.nutcracker.bamboo.application.model.query;

import java.io.Serial;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * 系统配置分页查询对象
 *
 * @author 胡桃夹子
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Schema(description = "系统配置分页查询对象")
public class ConfigQuery extends BasePageQuery {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "关键词(配置名称/配置键)", example = "system")
    private String keywords;

}
