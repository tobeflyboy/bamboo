package com.nutcracker.bamboo.infrastructure.persistence.repository.auth;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nutcracker.bamboo.domain.model.entity.Config;
import com.nutcracker.bamboo.infrastructure.persistence.entity.auth.ConfigDo;

/**
 * 系统配置对象转换器
 *
 * @author 胡桃夹子
 * @since 2024-7-29 11:42:49
 */
@org.mapstruct.Mapper(componentModel = "spring")
public interface ConfigConvert {

    ConfigConvert INSTANCE = org.mapstruct.factory.Mappers.getMapper(ConfigConvert.class);

    Page<Config> toPageVo(Page<ConfigDo> page);

    ConfigDo toEntity(Config sysConfig);

    Config toForm(ConfigDo configDo);
}
