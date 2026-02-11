package com.nutcracker.bamboo.infrastructure.mapper.auth;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nutcracker.bamboo.application.model.query.RoleQuery;
import com.nutcracker.bamboo.domain.model.entity.Role;
import com.nutcracker.bamboo.infrastructure.entity.auth.RoleDo;

/**
 * 角色Mapper
 *
 * @author 胡桃夹子
 * @date 2025/01/02 14:37:11
 */
@Mapper
public interface RoleMapper extends BaseMapper<RoleDo> {

    /**
     * 根据用户查询对应所有角色
     *
     * @param userId 用户
     * @return {@link List }<{@link RoleDo }>
     */
    List<RoleDo> findRoleByUserId(@Param("userId") String userId);

    /**
     * 根据编码查询角色
     *
     * @param roleCode 角色编码
     * @return {@link RoleDo }
     */
    RoleDo findRoleByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 查找系统角色
     *
     * @param query 系统角色
     * @return {@link List }<{@link Role }>
     */
    List<Role> findRole(RoleQuery query);

}
