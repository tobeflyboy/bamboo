package com.nutcracker.entity.convert.auth;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.nutcracker.entity.dataobject.auth.SysConfigDo;
import com.nutcracker.entity.domain.auth.SysConfig;

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
