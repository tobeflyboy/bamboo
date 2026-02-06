package com.nutcracker.bamboo.infrastructure.persistence.repository.auth;

import java.util.List;

import com.nutcracker.bamboo.domain.auth.model.SysRole;
import com.nutcracker.bamboo.infrastructure.persistence.entity.auth.SysRoleDo;

/**
 * 角色转换器
 *
 * @author 胡桃夹子
 * @date 2025/02/06 10:57:22
 */
@org.mapstruct.Mapper(componentModel = "spring")
public interface SysRoleConvert {

    SysRoleConvert INSTANCE = org.mapstruct.factory.Mappers.getMapper(SysRoleConvert.class);

    /**
     * domain转do
     *
     * @param role {@link SysRole}
     * @return {@link SysRoleDo}
     */
    SysRoleDo toDo(SysRole role);

    /**
     * domain转do
     *
     * @param list {@link List }<{@link SysRole }>
     * @return {@link List }<{@link SysRoleDo }>
     */
    List<SysRoleDo> toDo(List<SysRole> list);

    /**
     * do转domain
     *
     * @param roleDo {@link SysRoleDo}
     * @return {@link SysRole}
     */
    SysRole toDomain(SysRoleDo roleDo);

    /**
     * do转domain
     *
     * @param list {@link List }<{@link SysRoleDo }>
     * @return {@link List }<{@link SysRole }>
     */
    List<SysRole> toDomain(List<SysRoleDo> list);

}
