package com.nutcracker.bamboo.infrastructure.persistence.mapper.auth;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nutcracker.bamboo.application.dto.SysUserQuery;
import com.nutcracker.bamboo.domain.auth.model.SysUser;
import com.nutcracker.bamboo.infrastructure.persistence.entity.auth.SysUserDo;

/**
 * 用户Mapper
 *
 * @author 胡桃夹子
 * @date 2025/01/02 14:39:40
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserDo> {

    /**
     * 根据用户名查询用户
     *
     * @param user {@link SysUser }
     * @return {@link SysUser }
     */
    List<SysUser> findLoginUser(SysUser user);

    /**
     * 根据用户名查询用户
     *
     * @param roleCode 用户名
     * @return {@link List }<{@link SysUserDo }>
     */
    List<SysUserDo> findUserByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 根据条件（店铺、名称）查询客服人员
     *
     * @param query {@link SysUserQuery }
     * @return {@link List }<{@link SysUserDo }>
     */
    List<SysUser> findUser(SysUserQuery query);
}
