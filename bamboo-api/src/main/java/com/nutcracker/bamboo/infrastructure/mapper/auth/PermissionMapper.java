package com.nutcracker.bamboo.infrastructure.mapper.auth;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.nutcracker.bamboo.domain.model.entity.Permission;
import com.nutcracker.bamboo.infrastructure.entity.auth.PermissionDo;

/**
 * 菜单Mapper
 *
 * @author 胡桃夹子
 * @date 2025/01/02 14:33:39
 */
@Mapper
public interface PermissionMapper extends BaseMapper<PermissionDo> {

    /**
     * 按id更新菜单父编码
     *
     * @param newParentPermissionCode 新父权限代码
     * @param oldParentPermissionCode 旧父权限代码
     * @return int
     */
    int updateParentPermissionCode(String newParentPermissionCode, String oldParentPermissionCode);

    /**
     * 查找所有菜单
     *
     * @return {@link List }<{@link Permission }>
     */
    List<PermissionDo> findAll();

    /**
     * 查询角色所能访问的所有菜单
     *
     * @param roleId 角色id
     * @return {@link List }<{@link PermissionDo }>
     */
    List<PermissionDo> getPermissionByRoleId(@Param("roleId") String roleId);

    /**
     * 查询子菜单
     *
     * @param parentPermissionId 父菜单编码
     * @return {@link List }<{@link PermissionDo }>
     */
    List<PermissionDo> findByParentPermissionId(@Param("parentPermissionId") String parentPermissionId);

    /**
     * 查找全部菜单，同时判断角色有没有权限
     *
     * @param roleId 角色ID
     * @return {@link List }<{@link Permission }>
     */
    List<Permission> findAllByRoleId(@Param("roleId") String roleId);

}
