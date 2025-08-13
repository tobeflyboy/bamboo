package com.nutcracker.service.auth;

import com.github.pagehelper.PageInfo;
import com.nutcracker.common.wrapper.WrapperResp;
import com.nutcracker.entity.domain.auth.SaveRolePermission;
import com.nutcracker.entity.domain.auth.SysRole;

import java.util.List;

/**
 * 角色服务
 *
 * @author 胡桃夹子
 * @date 2025/01/02 15:16:46
 */
public interface SysRoleService {

    /**
     * 添加一个角色 ，若已经存在同名角色，则不创建
     *
     * @param sysRole 角色对象
     * @return {@link WrapperResp }<{@link Boolean }>
     */
    WrapperResp<Boolean> addRole(SysRole sysRole);

    /**
     * 根据编码查询角色
     *
     * @param code 角色编码
     * @return {@link SysRole }
     */
    SysRole findByRoleCode(String code);

    /**
     * 根据用户查询对应所有角色
     *
     * @param userId 用户Id
     * @return {@link List }<{@link SysRole }>
     */
    SysRole findRoleByUserId(String userId);

    /**
     * 给角色授权
     *
     * @param roleCode       角色编码
     * @param permissionCode 授权对应的KEY
     */
    void addRolePermission(String roleCode, String permissionCode);

    /**
     * 分页查询角色
     *
     * @param pageNum 当前页码
     * @param role    {@link SysRole }
     * @return {@link List }<{@link SysRole }>
     */
    PageInfo<SysRole> findSysRoleByPage(Integer pageNum, SysRole role);

    /**
     * 编辑角色
     *
     * @param role 角色
     * @return {@link WrapperResp }<{@link Boolean }>
     */
    WrapperResp<Boolean> editRole(SysRole role);

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
     * @return {@link WrapperResp }<{@link List }<{@link SysRole }>>
     */
    WrapperResp<List<SysRole>> roleList();
}
