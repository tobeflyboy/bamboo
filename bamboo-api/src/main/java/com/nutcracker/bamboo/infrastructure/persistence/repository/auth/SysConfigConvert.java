package com.nutcracker.bamboo.infrastructure.persistence.repository.auth;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nutcracker.bamboo.domain.auth.model.SysConfig;
import com.nutcracker.bamboo.infrastructure.persistence.entity.auth.SysConfigDo;

/**
 * 系统配置对象转换器
 *
 * @author 胡桃夹子
 * @since 2024-7-29 11:42:49
 */
@org.mapstruct.Mapper(componentModel = "spring")
public interface SysConfigConvert {

    SysConfigConvert INSTANCE = org.mapstruct.factory.Mappers.getMapper(SysConfigConvert.class);

    Page<SysConfig> toPageVo(Page<SysConfigDo> page);

    SysConfigDo toEntity(SysConfig sysConfig);

    SysConfig toForm(SysConfigDo configDo);
}
