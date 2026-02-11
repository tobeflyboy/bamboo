package com.nutcracker.bamboo.application.service.auth;

import java.util.List;

import com.github.pagehelper.PageInfo;
import com.nutcracker.bamboo.application.model.query.UserQuery;
import com.nutcracker.bamboo.common.wrapper.WrapperResp;
import com.nutcracker.bamboo.domain.model.entity.Role;
import com.nutcracker.bamboo.domain.model.entity.User;

/**
 * 用户服务
 *
 * @author 胡桃夹子
 * @date 2025/01/02 15:20:56
 */
public interface SysUserService {

    /**
     * 新增用户
     *
     * @param sysUser 用户
     * @param sysRole 角色
     */
    WrapperResp<Boolean> addSysUser(User sysUser, Role sysRole);



    /**
     * 更新密码
     *
     * @param user 用户对象
     */
    void updatePassword(User user);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return user 用户
     */
    User findByUsername(String username);



    /**
     * 更新用户上次登录时间
     *
     * @param sysUser 用户对照
     */
    void updateLastLoginTime(User sysUser);

    /**
     * 分页查询用户
     *
     * @param query  {@link UserQuery }
     * @return {@link List }<{@link User }>
     */
    PageInfo<User> findSysUserByPage(UserQuery query);

    /**
     * 删除用户
     *
     * @param userId 用户id
     * @return {@link WrapperResp }<{@link Boolean }>
     */
    WrapperResp<Boolean> deleteUser(String userId);

    /**
     * 编辑用户
     *
     * @param user 用户
     * @return {@link WrapperResp }<{@link Boolean }>
     */
    WrapperResp<Boolean> editUser(User user);

    /**
     * 重置用户密码
     *
     * @param user 用户
     * @return {@link WrapperResp }<{@link Boolean }>
     */
    WrapperResp<Boolean> resetPwd(User user);

    /**
     * 查找全部用户
     *
     * @param query 怎么翻译
     * @return {@link List }<{@link User }>
     */
    List<User> findAll(UserQuery query);


    /**
     * 查询用户
     *
     * @param userId 用户ID
     * @return {@link WrapperResp }<{@link User }>
     */
    WrapperResp<User> findById(String userId);
}
