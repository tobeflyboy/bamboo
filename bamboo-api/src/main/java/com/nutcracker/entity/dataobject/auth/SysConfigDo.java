package com.nutcracker.entity.dataobject.auth;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.nutcracker.mapper.CustomDateTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.ibatis.type.JdbcType;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统配置对象
 *
 * @author 胡桃夹子
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "系统配置")
@TableName("sys_config")
public class SysConfigDo implements Serializable {

    private static final long serialVersionUID = -8216754046436050522L;

    @Schema(description = "id", name = "id", requiredMode = Schema.RequiredMode.REQUIRED)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    @Schema(description = "配置名称", name = "configName")
    @TableField(value = "config_name", jdbcType = JdbcType.VARCHAR)
    private String configName;

    @Schema(description = "配置键", name = "configKey")
    @TableField(value = "config_key", jdbcType = JdbcType.VARCHAR)
    private String configKey;

    @Schema(description = "配置值", name = "configValue")
    @TableField(value = "config_value", jdbcType = JdbcType.VARCHAR)
    private String configValue;

    @Schema(description = "描述、备注", name = "remark")
    @TableField(value = "remark", jdbcType = JdbcType.VARCHAR)
    private String remark;

    @Schema(description = "创建人ID", name = "createBy")
    @TableField(value = "create_by", jdbcType = JdbcType.VARCHAR)
    private String createBy;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "创建时间", name = "createTime")
    @TableField(value = "create_time", fill = FieldFill.INSERT, typeHandler = CustomDateTypeHandler.class)
    private LocalDateTime createTime;

    @Schema(description = "更新人id", name = "update_by")
    @TableField(value = "update_by", jdbcType = JdbcType.VARCHAR)
    private String updateBy;

    @JsonInclude(value = JsonInclude.Include.NON_NULL)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "更新时间", name = "updateTime")
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE, typeHandler = CustomDateTypeHandler.class)
    private LocalDateTime updateTime;

    @Schema(description = "逻辑删除标识(0-未删除 1-已删除)", name = "isDeleted")
    @TableField(value = "is_deleted", jdbcType = JdbcType.VARCHAR)
    private Integer isDeleted;

}
