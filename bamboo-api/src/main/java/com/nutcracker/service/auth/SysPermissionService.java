package com.nutcracker.service.auth;

import com.nutcracker.entity.domain.auth.SysPermission;
import com.nutcracker.common.wrapper.WrapperResp;
import com.nutcracker.entity.vo.auth.RouteRecordRawVo;

import java.util.List;

/**
 * 权限服务
 *
 * @author 胡桃夹子
 * @date 2025/01/02 14:44:58
 */
public interface SysPermissionService {

    /**
     * 查找所有系统权限
     *
     * @return {@link List }<{@link RouteRecordRawVo }>
     */
    List<RouteRecordRawVo> findSysPermission();

    /**
     * 查询角色所能访问的所有菜单
     *
     * @param roleId 角色ID
     * @return {@link List }<{@link RouteRecordRawVo }>
     */
    List<RouteRecordRawVo> getRolePermissionByRoleId(String roleId);

    /**
     * 根据菜单ID，查询菜单资源
     *
     * @param id 菜单ID
     * @return {@link SysPermission }
     */
    SysPermission getPermission(String id);

    /**
     * 保存菜单
     *
     * @param sysPermission 菜单
     */
    WrapperResp<Boolean> savePermission(SysPermission sysPermission);

    /**
     * 删除菜单权限
     *
     * @param permissionId 菜单权限id
     * @return {@link WrapperResp }<{@link Boolean }>
     */
    WrapperResp<Boolean> deletePermission(String permissionId);

    /**
     * 查询角色所拥有的所有菜单
     *
     * @param roleId 角色ID
     * @return {@link List }<{@link RouteRecordRawVo }>
     */
    List<RouteRecordRawVo> getSysPermissionByRoleId(String roleId);

}
