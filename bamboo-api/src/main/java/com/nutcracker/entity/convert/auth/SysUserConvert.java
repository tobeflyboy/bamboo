package com.nutcracker.entity.convert.auth;

import com.nutcracker.entity.dataobject.auth.SysUserDo;
import com.nutcracker.entity.domain.auth.OnlineUser;
import com.nutcracker.entity.domain.auth.SysUser;

import java.util.List;

/**
 * 用户转换器
 *
 * @author 胡桃夹子
 * @date 2025/02/06 10:57:22
 */
@org.mapstruct.Mapper(componentModel = "spring")
public interface SysUserConvert {

    SysUserConvert INSTANCE = org.mapstruct.factory.Mappers.getMapper(SysUserConvert.class);

    /**
     * SysUser 转 OnlineUser
     *
     * @param user {@link SysUser}
     * @return {@link OnlineUser}
     */
    OnlineUser toOnlineUser(SysUser user);

    /**
     * domain转do
     *
     * @param user {@link SysUser}
     * @return {@link SysUserDo}
     */
    SysUserDo toDo(SysUser user);

    /**
     * domain转do
     *
     * @param list {@link List }<{@link SysUser }>
     * @return {@link List }<{@link SysUserDo }>
     */
    List<SysUserDo> toDo(List<SysUser> list);

    /**
     * do转domain
     *
     * @param userDo {@link SysUserDo}
     * @return {@link SysUser}
     */
    SysUser toDomain(SysUserDo userDo);

    /**
     * do转domain
     *
     * @param list {@link List }<{@link SysUserDo }>
     * @return {@link List }<{@link SysUser }>
     */
    List<SysUser> toDomain(List<SysUserDo> list);

}
