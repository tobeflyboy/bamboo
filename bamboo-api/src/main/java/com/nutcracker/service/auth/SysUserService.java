package com.nutcracker.service.auth;

import com.github.pagehelper.PageInfo;
import com.nutcracker.common.wrapper.WrapperResp;
import com.nutcracker.entity.domain.auth.SysRole;
import com.nutcracker.entity.domain.auth.SysUser;

import java.util.List;

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
    WrapperResp<Boolean> addSysUser(SysUser sysUser, SysRole sysRole);

    /**
     * 注册或绑定微信用户
     *
     * @param openid 微信openid
     */
    void registerOrBindWechatUser(String openid);

    /**
     * 按手机注册用户并打开id
     *
     * @param mobile 手机号
     * @param openid 微信openid
     */
    boolean registerUserByMobileAndOpenId(String mobile, String openid);

    /**
     * 用户绑定微信openid
     *
     * @param userId 用户id
     * @param openid 微信openid
     */
    void bindUserOpenId(String userId, String openid);

    /**
     * 更新密码
     *
     * @param user 用户对象
     */
    void updatePassword(SysUser user);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return user 用户
     */
    SysUser findByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param mobile 手机号
     * @return user 用户
     */
    SysUser findByMobile(String mobile);

    /**
     * 根据微信openid查询用户
     *
     * @param openid 微信openid
     * @return user 用户
     */
    SysUser findByOpenid(String openid);

    /**
     * 更新用户上次登录时间
     *
     * @param sysUser 用户对照
     */
    void updateLastLoginTime(SysUser sysUser);

    /**
     * 分页查询用户
     *
     * @param pageNum 当前页码
     * @param user  {@link SysUser }
     * @return {@link List }<{@link SysUser }>
     */
    PageInfo<SysUser> findSysUserByPage(Integer pageNum, SysUser user);

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
    WrapperResp<Boolean> editUser(SysUser user);

    /**
     * 重置用户密码
     *
     * @param user 用户
     * @return {@link WrapperResp }<{@link Boolean }>
     */
    WrapperResp<Boolean> resetPwd(SysUser user);

    /**
     * 查找全部用户
     *
     * @return {@link List }<{@link SysUser }>
     */
    List<SysUser> findAll(SysUser user);
}
