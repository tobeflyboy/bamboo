package com.nutcracker.bamboo.application.service.auth.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nutcracker.bamboo.application.model.query.UserQuery;
import com.nutcracker.bamboo.application.service.auth.SysUserService;
import com.nutcracker.bamboo.common.constant.DemoConstants;
import com.nutcracker.bamboo.common.constant.PrimaryKey;
import com.nutcracker.bamboo.common.enums.SysUserStatusEnum;
import com.nutcracker.bamboo.common.exception.BusinessException;
import com.nutcracker.bamboo.common.util.JSON;
import com.nutcracker.bamboo.common.util.salt.Digests;
import com.nutcracker.bamboo.common.util.salt.Encodes;
import com.nutcracker.bamboo.common.wrapper.WrapperResp;
import com.nutcracker.bamboo.domain.model.entity.Role;
import com.nutcracker.bamboo.domain.model.entity.User;
import com.nutcracker.bamboo.infrastructure.converter.auth.RoleConvert;
import com.nutcracker.bamboo.infrastructure.converter.auth.UserConvert;
import com.nutcracker.bamboo.infrastructure.entity.auth.RoleDo;
import com.nutcracker.bamboo.infrastructure.entity.auth.UserDo;
import com.nutcracker.bamboo.infrastructure.entity.auth.UserRoleDo;
import com.nutcracker.bamboo.infrastructure.mapper.CustomDateTypeHandler;
import com.nutcracker.bamboo.infrastructure.mapper.auth.RoleMapper;
import com.nutcracker.bamboo.infrastructure.mapper.auth.UserMapper;
import com.nutcracker.bamboo.infrastructure.mapper.auth.UserRoleMapper;
import com.nutcracker.bamboo.web.Identify;
import com.nutcracker.bamboo.web.security.util.SecurityUtils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 用户服务impl
 *
 * @author 胡桃夹子
 * @date 2025/01/02 15:21:01
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysUserServiceImpl implements SysUserService {
    public static final int HASH_INTERATIONS = 1024;
    private static final int SALT_SIZE = 8;

    private final RoleMapper sysRoleMapper;
    private final UserRoleMapper sysUserRoleMapper;
    private final UserMapper sysUserMapper;

    /**
     * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
     */
    private void entryptPassword(UserDo sysUserDo) {
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        sysUserDo.setSalt(Encodes.encodeHex(salt));
        String pwd = SecurityUtils.encryptPassword(sysUserDo.getSalt(), sysUserDo.getPassword(), sysUserDo.getUsername());
        sysUserDo.setPassword(pwd);
    }

    @Transactional
    @Override
    public WrapperResp<Boolean> addSysUser(User user, Role role) {
        log.info("addSysUser {},{}", user, role);
        if (user == null || role == null) {
            log.error("addSysUser 缺少必要参数，新增用户失败！");
            return WrapperResp.validateFailed("缺少必要参数，新增用户失败！");
        }

        if (StrUtil.isBlank(user.getUsername()) || StrUtil.isBlank(user.getPassword())) {
            log.error("addSysUser {},{} 添加用户失败，新增用户失败！", user, role);
            return WrapperResp.validateFailed("账号或密码错误，新增用户失败！");
        }

        if (StrUtil.isBlank(role.getId())) {
            log.error("addSysUser {},{} 用户未指定所属角色，新增用户失败！", user, role);
            return WrapperResp.validateFailed("用户未指定所属角色，新增用户失败！");
        }

        RoleDo r = sysRoleMapper.selectById(role.getId());
        if (r == null) {
            log.error("addSysUser {},{} 用户未指定所属组织或角色，新增用户失败！", user, role);
            return WrapperResp.validateFailed("用户未指定所属组织或角色，新增用户失败！");
        }

        List<User> userList = sysUserMapper.findLoginUser(user);
        if (CollUtil.isNotEmpty(userList)) {
            log.error("addSysUser {},{} 用户账号已经存在，新增用户失败！", user, role);
            return WrapperResp.failed("用户账号已经存在，新增用户失败！");
        }
        UserDo u = UserConvert.INSTANCE.toDo(user);
        r = RoleConvert.INSTANCE.toDo(role);

        String createdBy = Identify.getSessionUser().getUserId();
        LocalDateTime now = LocalDateTime.now();
        entryptPassword(u);
        u.setId(PrimaryKey.getSysUserId());
        u.setStatus(SysUserStatusEnum.VALID.getCode());
        u.setCreateTime(now);
        u.setCreateBy(createdBy);
        int ret = sysUserMapper.insert(u);
        if (ret == 0) {
            log.error("addSysUser {},{} 新增用户失败！", u, r);
            return WrapperResp.failed("新增用户失败！");
        }

        UserRoleDo ur = new UserRoleDo();
        ur.setId(PrimaryKey.getSysUserRoleId());
        ur.setRoleId(r.getId());
        ur.setUserId(u.getId());
        ur.setCreateTime(now);
        ur.setCreateBy(createdBy);
        ret = sysUserRoleMapper.insert(ur);
        if (ret == 0) {
            log.error("addSysUser {},{} 新增用户失败！", u, r);
            return WrapperResp.failed("新增用户失败！");
        }
        return WrapperResp.success(true);
    }



    @Override
    public void updatePassword(User sysUser) {
        UserDo u = sysUserMapper.selectById(sysUser.getUserId());
        u.setPassword(sysUser.getPassword());
        entryptPassword(u);
        u.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(u);
    }

    @Override
    public User findByUsername(String username) {
        try {
            User user = User.builder().username(username).build();
            List<User> list = sysUserMapper.findLoginUser(user);
            if (CollUtil.isNotEmpty(list)) {
                return list.iterator().next();
            }
            return null;
        } catch (Exception e) {
            log.error("# 根据账号查询用户报错 , username={}", username);
            throw new BusinessException("1001", "查询用户失败");
        }
    }



    @Transactional
    @Override
    public void updateLastLoginTime(User sysUser) {
        if (null == sysUser || null == sysUser.getUserId()) {
            return;
        }
        UserDo u = sysUserMapper.selectById(sysUser.getUserId());
        if (u != null) {
            LocalDateTime lastLoginTime = sysUser.getLastLoginTime();
            if (lastLoginTime == null) {
                lastLoginTime = LocalDateTime.now();
            }
            UserDo sysUserDo = new UserDo();
            sysUserDo.setId(u.getId());
            sysUserDo.setLastLoginTime(lastLoginTime);
            sysUserMapper.updateById(sysUserDo);
        }
    }

    @Override
    public PageInfo<User> findSysUserByPage(UserQuery query) {
        log.info("findSysUserByPage , query={}", query);
        int pageNum = Optional.ofNullable(query).orElse(new UserQuery()).getPageNum();
        PageHelper.startPage(pageNum, DemoConstants.PAGE_SIZE);
        List<User> list = sysUserMapper.findUser(query);
        log.info("findSysUserByPage, list={}", JSON.toJSONString(list));
        PageInfo<User> page = new PageInfo<>(list);
        log.debug("findSysUserByPage page.toString()={}", page);
        return page;
    }

    @Transactional
    @Override
    public WrapperResp<Boolean> deleteUser(String userId) {
        log.info("deleteUser , userId={}", userId);
        if (StrUtil.isBlank(userId)) {
            return WrapperResp.validateFailed("删除失败，用户id为空！");
        }
        UserDo userDo = sysUserMapper.selectById(userId);
        if (null == userDo) {
            return WrapperResp.validateFailed("删除失败，用户不存在！");
        }

        List<UserRoleDo> list = sysUserRoleMapper.findUserRoleByUserId(userId);
        if (CollUtil.isNotEmpty(list)) {
            int ret = sysUserRoleMapper.delete(new LambdaUpdateWrapper<UserRoleDo>().eq(UserRoleDo::getUserId, userId));
            if (ret == 0) {
                log.error("deleteUser, sysUserRoleMapper.delete fail, userId={}", userId);
                return WrapperResp.failed("删除失败！");
            }
        }
        if (0 == sysUserMapper.deleteById(userId)) {
            return WrapperResp.failed("删除失败！");
        }
        return WrapperResp.success(true);
    }

    @Transactional
    @Override
    public WrapperResp<Boolean> editUser(User user) {
        log.info("editUser, {}", user);
        if (ObjectUtil.isEmpty(user) || StrUtil.isBlank(user.getUserId())) {
            return WrapperResp.validateFailed("编辑保存失败，缺失用户信息！");
        }
        if (StrUtil.isBlank(user.getRealName())) {
            return WrapperResp.validateFailed("编辑保存失败，姓名为必填项！");
        }
        if (StrUtil.isBlank(user.getRoleId())) {
            return WrapperResp.validateFailed("编辑保存失败，角色必选！");
        }
        if (StrUtil.isBlank(user.getEmail())) {
            return WrapperResp.validateFailed("编辑保存失败，邮箱为必填项！");
        }
        if (ObjectUtil.isEmpty(user.getStatus())) {
            return WrapperResp.validateFailed("编辑保存失败，用户状态未指定！");
        }
        UserDo userDo = sysUserMapper.selectById(user.getUserId());
        if (null == userDo) {
            return WrapperResp.validateFailed("编辑保存失败，用户不存在！");
        }

        LocalDateTime now = LocalDateTime.now();
        String operator = Identify.getSessionUser().getUserId();
        // 更新用户状态、邮箱
        //int updateResult = sysUserMapper.update(
        //        new LambdaUpdateWrapper<SysUserDo>()
        //                .eq(SysUserDo::getId, user.getUserId())
        //                .set(SysUserDo::getRealName, user.getRealName())
        //                .set(SysUserDo::getStatus, user.getStatus())
        //                .set(SysUserDo::getEmail, user.getEmail())
        //                .set(SysUserDo::getUpdateTime, now, "typeHandler=" + CustomDateTypeHandler.class.getName())
        //                .set(SysUserDo::getUpdateBy, operator)
        //);
        UserDo updateEntity = new UserDo();
        updateEntity.setId(user.getUserId());
        updateEntity.setRealName(user.getRealName());
        updateEntity.setStatus(user.getStatus());
        updateEntity.setEmail(user.getEmail());
        updateEntity.setUpdateTime(now);
        updateEntity.setUpdateBy(operator);
        int updateResult = sysUserMapper.updateById(updateEntity);
        if (updateResult == 0) {
            log.error("editUser, sysUserMapper.update fail, {},now={},operator={}", user, now, operator);
            return WrapperResp.failed("编辑保存失败！");
        }
        List<UserRoleDo> list = sysUserRoleMapper.findUserRoleByUserId(user.getUserId());
        if (CollUtil.isEmpty(list)) {
            // 用户找不到角色，给用户新增角色
            UserRoleDo userRoleDo = UserRoleDo.builder()
                    .roleId(user.getRoleId())
                    .userId(user.getUserId())
                    .createTime(now)
                    .createBy(operator)
                    .build();
            if (sysUserRoleMapper.insert(userRoleDo) == 0) {
                log.error("editUser, sysUserRoleMapper.insert fail, {},now={},operator={}", userRoleDo, now, operator);
                return WrapperResp.failed("编辑保存失败！");
            }
        } else {
            if (CollUtil.size(list) > 1) {
                // 清理冗余角色记录（保留第一条）
                list.subList(1, list.size()).forEach(role ->
                        sysUserRoleMapper.deleteById(role.getId())
                );
            }
            UserRoleDo userRoleDo = list.get(0);
            if (!StrUtil.equals(user.getRoleId(), userRoleDo.getRoleId())) {
                userRoleDo.setRoleId(user.getRoleId());
                if (sysUserRoleMapper.updateById(userRoleDo) == 0) {
                    log.error("editUser, sysUserRoleMapper.updateById fail, {},now={},operator={}", userRoleDo, now, operator);
                    return WrapperResp.failed("编辑保存失败！");
                }
            }
        }
        return WrapperResp.success(true);
    }

    @Transactional
    @Override
    public WrapperResp<Boolean> resetPwd(User user) {
        log.info("resetPwd, {}", user);
        if (ObjectUtil.isEmpty(user) || StrUtil.isBlank(user.getUserId())) {
            return WrapperResp.validateFailed("重置密码失败，缺失用户信息！");
        }
        if (StrUtil.isBlank(user.getNewPassword())) {
            return WrapperResp.validateFailed("请输入密码！");
        }
        UserDo userDo = sysUserMapper.selectById(user.getUserId());
        if (null == userDo) {
            return WrapperResp.validateFailed("重置密码失败，用户不存在！");
        }
        String password = SecurityUtils.encryptPassword(userDo.getSalt(), user.getNewPassword(), userDo.getUsername());
        LocalDateTime now = LocalDateTime.now();
        String operator = Identify.getSessionUser().getUserId();
        // 更新用户状态、邮箱
        int updateResult = sysUserMapper.update(
                new LambdaUpdateWrapper<UserDo>()
                        .eq(UserDo::getId, user.getUserId())
                        .set(UserDo::getPassword, password)
                        .set(UserDo::getUpdateTime, now, "typeHandler=" + CustomDateTypeHandler.class.getName())
                        .set(UserDo::getUpdateBy, operator)
        );
        if (updateResult == 0) {
            log.error("resetPwd, sysUserMapper.update fail, {},now={},operator={}", user, now, operator);
            return WrapperResp.failed("重置密码失败！");
        }
        return WrapperResp.success(true);
    }

    @Override
    public List<User> findAll(UserQuery query) {
        return sysUserMapper.findUser(query);
    }


    @Override
    public WrapperResp<User> findById(String userId) {
        if (StrUtil.isBlank(userId)) {
            return WrapperResp.validateFailed("用户ID为空！");
        }
        UserQuery query = UserQuery.builder().userId(userId).build();
        List<User> list = sysUserMapper.findUser(query);
        if (CollUtil.isEmpty(list)) {
            log.warn("未找到用户信息, userId={}", userId);
            return WrapperResp.failed("未找到用户信息");
        }
        return WrapperResp.success(list.iterator().next());
    }
}
