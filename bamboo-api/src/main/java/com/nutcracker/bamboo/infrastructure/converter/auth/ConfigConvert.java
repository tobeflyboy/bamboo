package com.nutcracker.bamboo.infrastructure.converter.auth;

import java.util.List;
import com.nutcracker.bamboo.domain.model.entity.Config;
import com.nutcracker.bamboo.infrastructure.entity.auth.ConfigDo;

/**
 * 系统配置对象转换器
 * 
 * @author 胡桃夹子
 * @since 2024-7-29 11:42:49
 */
@org.mapstruct.Mapper(componentModel = "spring")
public interface ConfigConvert {

    ConfigConvert INSTANCE = org.mapstruct.factory.Mappers.getMapper(ConfigConvert.class);

    @org.mapstruct.Mappings({
            @org.mapstruct.Mapping(target = "createBy", ignore = true),
            @org.mapstruct.Mapping(target = "createTime", ignore = true),
            @org.mapstruct.Mapping(target = "updateTime", ignore = true),
            @org.mapstruct.Mapping(target = "updateBy", ignore = true),
            @org.mapstruct.Mapping(target = "isDeleted", ignore = true)
    })
    ConfigDo toEntity(Config config);

    Config toForm(ConfigDo configDo);

    List<Config> toConfigList(List<ConfigDo> configDoList);
}
