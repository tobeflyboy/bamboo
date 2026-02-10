package com.nutcracker.bamboo.infrastructure.persistence.mapper.auth;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nutcracker.bamboo.application.model.query.UserQuery;
import com.nutcracker.bamboo.domain.model.entity.User;
import com.nutcracker.bamboo.infrastructure.persistence.entity.auth.UserDo;

/**
 * 用户Mapper
 *
 * @author 胡桃夹子
 * @date 2025/01/02 14:39:40
 */
@Mapper
public interface UserMapper extends BaseMapper<UserDo> {

    /**
     * 根据用户名查询用户
     *
     * @param user {@link SysUser }
     * @return {@link SysUser }
     */
    List<User> findLoginUser(User user);

    /**
     * 根据用户名查询用户
     *
     * @param roleCode 用户名
     * @return {@link List }<{@link UserDo }>
     */
    List<UserDo> findUserByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 根据条件（店铺、名称）查询客服人员
     *
     * @param query {@link UserQuery }
     * @return {@link List }<{@link UserDo }>
     */
    List<User> findUser(UserQuery query);
}
