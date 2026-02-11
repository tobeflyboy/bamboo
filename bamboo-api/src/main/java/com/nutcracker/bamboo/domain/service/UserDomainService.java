package com.nutcracker.bamboo.domain.service;

import com.nutcracker.bamboo.domain.model.entity.User;

/**
 * 用户领域服务接口
 * 处理跨越多个聚合根的业务逻辑
 *
 * @author 胡桃夹子
 * @since 2026-02-09
 */
public interface UserDomainService {
    
    /**
     * 验证用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @return 用户对象
     * @throws SecurityException 验证失败时抛出
     */
    User authenticate(String username, String password) throws SecurityException;
    
    /**
     * 修改用户密码
     * 
     * @param userId 用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @throws SecurityException 密码验证失败时抛出
     */
    void changePassword(String userId, String oldPassword, String newPassword) throws SecurityException;
    
    /**
     * 锁定用户账户
     * 
     * @param userId 用户ID
     */
    void lockUser(String userId);
    
    /**
     * 解锁用户账户
     * 
     * @param userId 用户ID
     */
    void unlockUser(String userId);
}