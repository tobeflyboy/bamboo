package com.nutcracker.bamboo.domain.repository;

import java.util.List;
import java.util.Optional;

import com.nutcracker.bamboo.domain.model.entity.Role;

/**
 * 角色仓储接口
 * DDD Repository模式，定义领域对象的持久化操作
 *
 * @author 胡桃夹子
 * @since 2026-02-09
 */
public interface RoleRepository {
    
    /**
     * 根据ID查找角色
     * 
     * @param roleId 角色ID
     * @return 角色对象
     */
    Optional<Role> findById(String roleId);
    
    /**
     * 根据角色编码查找角色
     * 
     * @param roleCode 角色编码
     * @return 角色对象
     */
    Optional<Role> findByRoleCode(String roleCode);
    
    /**
     * 保存角色
     * 
     * @param role 角色对象
     * @return 保存后的角色
     */
    Role save(Role role);
    
    /**
     * 删除角色
     * 
     * @param roleId 角色ID
     */
    void deleteById(String roleId);
    
    /**
     * 查找所有角色
     * 
     * @return 角色列表
     */
    List<Role> findAll();
    
    /**
     * 根据状态查找角色
     * 
     * @param status 状态
     * @return 角色列表
     */
    List<Role> findByStatus(Integer status);
}