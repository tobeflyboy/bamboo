package com.nutcracker.bamboo.infrastructure.persistence.repository.auth;

import java.util.List;

import com.nutcracker.bamboo.domain.model.entity.User;
import com.nutcracker.bamboo.domain.model.valueobject.OnlineUser;
import com.nutcracker.bamboo.infrastructure.persistence.entity.auth.UserDo;

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
     * User 转 OnlineUser
     *
     * @param user {@link User}
     * @return {@link OnlineUser}
     */
    OnlineUser toOnlineUser(User user);

    /**
     * domain转do
     *
     * @param user {@link User}
     * @return {@link UserDo}
     */
    UserDo toDo(User user);

    /**
     * domain转do
     *
     * @param list {@link List }<{@link User }>
     * @return {@link List }<{@link UserDo }>
     */
    List<UserDo> toDo(List<User> list);

    /**
     * do转domain
     *
     * @param userDo {@link UserDo}
     * @return {@link User}
     */
    User toDomain(UserDo userDo);

    /**
     * do转domain
     *
     * @param list {@link List }<{@link UserDo }>
     * @return {@link List }<{@link User }>
     */
    List<User> toDomain(List<UserDo> list);

}
