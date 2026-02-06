package com.nutcracker.bamboo.infrastructure.persistence.mapper.auth;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nutcracker.bamboo.application.dto.SysRoleQuery;
import com.nutcracker.bamboo.domain.auth.model.SysRole;
import com.nutcracker.bamboo.infrastructure.persistence.entity.auth.SysRoleDo;

/**
 * 角色Mapper
 *
 * @author 胡桃夹子
 * @date 2025/01/02 14:37:11
 */
@Mapper
public interface SysRoleMapper extends BaseMapper<SysRoleDo> {

    /**
     * 根据用户查询对应所有角色
     *
     * @param userId 用户
     * @return {@link List }<{@link SysRoleDo }>
     */
    List<SysRoleDo> findRoleByUserId(@Param("userId") String userId);

    /**
     * 根据编码查询角色
     *
     * @param roleCode 角色编码
     * @return {@link SysRoleDo }
     */
    SysRoleDo findRoleByRoleCode(@Param("roleCode") String roleCode);

    /**
     * 查找系统角色
     *
     * @param query 系统角色
     * @return {@link List }<{@link SysRole }>
     */
    List<SysRole> findSysRole(SysRoleQuery query);

}
