package com.nutcracker.entity.query.auth;

import com.nutcracker.entity.query.BasePageQuery;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 系统配置查询对象
 *
 * @author 胡桃夹子
 */
@Getter
@Setter
@ToString(callSuper = true)
@Schema(description = "系统配置分页查询")
public class ConfigPageQuery extends BasePageQuery {

    private static final long serialVersionUID = -239862668448262548L;

    @Schema(description = "关键字(配置项名称/配置项值)")
    private String keywords;
}
