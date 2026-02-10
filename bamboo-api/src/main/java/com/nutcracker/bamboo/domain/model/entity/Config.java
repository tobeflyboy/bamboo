package com.nutcracker.bamboo.domain.model.entity;

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

public class Config {

    
    private String id;

    
    private String configName;

    
    private String configKey;

    
    private String configValue;

    
    private String remark;
}
