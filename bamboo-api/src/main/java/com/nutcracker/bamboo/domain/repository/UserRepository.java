package com.nutcracker.bamboo.domain.repository;

import java.util.List;
import java.util.Optional;

import com.nutcracker.bamboo.domain.model.entity.User;

/**
 * 用户仓储接口
 * DDD Repository模式，定义领域对象的持久化操作
 *
 * @author 胡桃夹子
 * @since 2026-02-09
 */
public interface UserRepository {
    
    /**
     * 根据ID查找用户
     * 
     * @param userId 用户ID
     * @return 用户对象
     */
    Optional<User> findById(String userId);
    
    /**
     * 根据用户名查找用户
     * 
     * @param username 用户名
     * @return 用户对象
     */
    Optional<User> findByUsername(String username);
    
    /**
     * 保存用户
     * 
     * @param user 用户对象
     * @return 保存后的用户
     */
    User save(User user);
    
    /**
     * 删除用户
     * 
     * @param userId 用户ID
     */
    void deleteById(String userId);
    
    /**
     * 查找所有用户
     * 
     * @return 用户列表
     */
    List<User> findAll();
    
    /**
     * 根据状态查找用户
     * 
     * @param status 状态
     * @return 用户列表
     */
    List<User> findByStatus(Integer status);
}