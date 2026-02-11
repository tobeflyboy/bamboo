package com.nutcracker.bamboo.infrastructure.converter.auth;

import java.util.List;

import com.nutcracker.bamboo.domain.model.entity.Role;
import com.nutcracker.bamboo.infrastructure.entity.auth.RoleDo;

/**
 * 角色转换器
 *
 * @author 胡桃夹子
 * @date 2025/02/06 10:57:22
 */
@org.mapstruct.Mapper(componentModel = "spring")
public interface RoleConvert {

    RoleConvert INSTANCE = org.mapstruct.factory.Mappers.getMapper(RoleConvert.class);

    /**
     * domain转do
     *
     * @param role {@link Role}
     * @return {@link RoleDo}
     */
    RoleDo toDo(Role role);

    /**
     * domain转do
     *
     * @param list {@link List }<{@link Role }>
     * @return {@link List }<{@link RoleDo }>
     */
    List<RoleDo> toDo(List<Role> list);

    /**
     * do转domain
     *
     * @param roleDo {@link RoleDo}
     * @return {@link Role}
     */
    Role toDomain(RoleDo roleDo);

    /**
     * do转domain
     *
     * @param list {@link List }<{@link RoleDo }>
     * @return {@link List }<{@link Role }>
     */
    List<Role> toDomain(List<RoleDo> list);

}
