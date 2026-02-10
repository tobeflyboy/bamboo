package com.nutcracker.bamboo.application.service.auth;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.nutcracker.bamboo.application.model.query.RoleQuery;
import com.nutcracker.bamboo.common.wrapper.WrapperResp;
import com.nutcracker.bamboo.domain.model.command.SaveRolePermission;
import com.nutcracker.bamboo.domain.model.entity.Role;

/**
 * 角色服务
 *
 * @author 胡桃夹子
 * @date 2025/01/02 15:16:46
 */
public interface RoleService {

    /**
     * 添加一个角色 ，若已经存在同名角色，则不创建
     *
     * @param sysRole 角色对象
     * @return {@link WrapperResp }<{@link Boolean }>
     */
    WrapperResp<Boolean> addRole(Role sysRole);

    /**
     * 根据编码查询角色
     *
     * @param code 角色编码
     * @return {@link Role }
     */
    Role findByRoleCode(String code);

    /**
     * 根据用户查询对应所有角色
     *
     * @param userId 用户Id
     * @return {@link List }<{@link Role }>
     */
    Role findRoleByUserId(String userId);

    /**
     * 给角色授权
     *
     * @param roleId       角色id
     * @param permissionId 资源id
     */
    void addRolePermission(String roleId, String permissionId);

    /**
     * 分页查询角色
     *
     * @param query    {@link RoleQuery }
     * @return {@link List }<{@link Role }>
     */
    PageInfo<Role> findRoleByPage(RoleQuery query);

    /**
     * 编辑角色
     *
     * @param role 角色
     * @return {@link WrapperResp }<{@link Boolean }>
     */
    WrapperResp<Boolean> editRole(Role role);

    /**
     * 给角色授权
     *
     * @param saveRolePermission 保存角色权限
     * @return {@link WrapperResp }<{@link Boolean }>
     */
    WrapperResp<Boolean> saveRolePermission(SaveRolePermission saveRolePermission);

    /**
     * 删除角色
     *
     * @param roleId 角色ID
     * @return {@link WrapperResp }<{@link Boolean }>
     */
    WrapperResp<Boolean> deleteRole(String roleId);

    /**
     * 角色列表
     *
     * @return {@link WrapperResp }<{@link List }<{@link Role }>>
     */
    WrapperResp<List<Role>> roleList();
}
