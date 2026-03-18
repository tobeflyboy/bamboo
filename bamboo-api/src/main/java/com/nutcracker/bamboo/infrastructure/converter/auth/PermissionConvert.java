package com.nutcracker.bamboo.infrastructure.converter.auth;

import java.util.List;
import com.nutcracker.bamboo.domain.model.entity.Permission;
import com.nutcracker.bamboo.infrastructure.entity.auth.PermissionDo;

/**
 * 角色转换器
 *
 * @author 胡桃夹子
 * @date 2025/02/06 10:57:22
 */
@org.mapstruct.Mapper(componentModel = "spring")
public interface PermissionConvert {

    PermissionConvert INSTANCE = org.mapstruct.factory.Mappers.getMapper(PermissionConvert.class);

    /**
     * domain转do
     *
     * @param role {@link Permission}
     * @return {@link PermissionDo}
     */
    PermissionDo toDo(Permission role);

    /**
     * domain转do
     *
     * @param list {@link List }<{@link Permission }>
     * @return {@link List }<{@link PermissionDo }>
     */
    List<PermissionDo> toDo(List<Permission> list);

    /**
     * do转domain
     *
     * @param roleDo {@link PermissionDo}
     * @return {@link Permission}
     */
    @org.mapstruct.Mappings({
            @org.mapstruct.Mapping(target = "checked", ignore = true)
    })
    Permission toDomain(PermissionDo roleDo);

    /**
     * do转domain
     *
     * @param list {@link List }<{@link PermissionDo }>
     * @return {@link List }<{@link Permission }>
     */
    List<Permission> toDomain(List<PermissionDo> list);

}
