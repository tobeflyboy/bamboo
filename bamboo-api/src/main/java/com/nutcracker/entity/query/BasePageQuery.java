package com.nutcracker.entity.query;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * 基础分页请求对象
 *
 * @author 胡桃夹子
 */
@Data
@ToString(callSuper = true)
@Schema
public class BasePageQuery implements Serializable {

    private static final long serialVersionUID = 1478111635617893712L;

    @Schema(description = "页码", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private int pageNum = 1;

    @Schema(description = "每页记录数", requiredMode = Schema.RequiredMode.REQUIRED, example = "10")
    private int pageSize = 10;

}
