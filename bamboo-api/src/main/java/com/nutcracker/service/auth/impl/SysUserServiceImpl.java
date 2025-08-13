package com.nutcracker.service.auth.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nutcracker.common.enums.SysUserStatusEnum;
import com.nutcracker.common.exception.BusinessException;
import com.nutcracker.common.wrapper.WrapperResp;
import com.nutcracker.constant.DemoConstants;
import com.nutcracker.entity.convert.auth.SysRoleConvert;
import com.nutcracker.entity.convert.auth.SysUserConvert;
import com.nutcracker.entity.dataobject.auth.SysRoleDo;
import com.nutcracker.entity.dataobject.auth.SysUserDo;
import com.nutcracker.entity.dataobject.auth.SysUserRoleDo;
import com.nutcracker.entity.domain.auth.SysRole;
import com.nutcracker.entity.domain.auth.SysUser;
import com.nutcracker.mapper.CustomDateTypeHandler;
import com.nutcracker.mapper.auth.SysRoleMapper;
import com.nutcracker.mapper.auth.SysUserMapper;
import com.nutcracker.mapper.auth.SysUserRoleMapper;
import com.nutcracker.service.auth.SysUserService;
import com.nutcracker.util.salt.Digests;
import com.nutcracker.util.salt.Encodes;
import com.nutcracker.web.Identify;
import com.nutcracker.web.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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

    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysUserMapper sysUserMapper;

    /**
     * 设定安全的密码，生成随机的salt并经过1024次 sha-1 hash
     */
    private void entryptPassword(SysUserDo sysUserDo) {
        byte[] salt = Digests.generateSalt(SALT_SIZE);
        sysUserDo.setSalt(Encodes.encodeHex(salt));
        String pwd = SecurityUtils.encryptPassword(sysUserDo.getSalt(), sysUserDo.getPassword(), sysUserDo.getUsername());
        sysUserDo.setPassword(pwd);
    }

    @Transactional
    @Override
    public WrapperResp<Boolean> addSysUser(SysUser user, SysRole role) {
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

        SysRoleDo r = sysRoleMapper.selectById(role.getId());
        if (r == null) {
            log.error("addSysUser {},{} 用户未指定所属组织或角色，新增用户失败！", user, role);
            return WrapperResp.validateFailed("用户未指定所属组织或角色，新增用户失败！");
        }

        List<SysUser> userList = sysUserMapper.findLoginUser(user);
        if (CollUtil.isNotEmpty(userList)) {
            log.error("addSysUser {},{} 用户账号已经存在，新增用户失败！", user, role);
            return WrapperResp.failed("用户账号已经存在，新增用户失败！");
        }
        SysUserDo u = SysUserConvert.INSTANCE.toDo(user);
        r = SysRoleConvert.INSTANCE.toDo(role);

        String createdBy = Identify.getSessionUser().getUserId();
        LocalDateTime now = LocalDateTime.now();
        entryptPassword(u);
        u.setId(String.valueOf(IdWorker.getId("sys_user")));
        u.setStatus(SysUserStatusEnum.VALID.getCode());
        u.setCreateTime(now);
        u.setCreateBy(createdBy);
        int ret = sysUserMapper.insert(u);
        if (ret == 0) {
            log.error("addSysUser {},{} 新增用户失败！", u, r);
            return WrapperResp.failed("新增用户失败！");
        }

        SysUserRoleDo ur = new SysUserRoleDo();
        ur.setId(String.valueOf(IdWorker.getId("sys_user_role")));
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
    public void registerOrBindWechatUser(String openid) {
        // TODO register
    }

    @Override
    public boolean registerUserByMobileAndOpenId(String mobile, String openid) {
        // TODO register
        return false;
    }

    @Override
    public void bindUserOpenId(String userId, String openid) {
        // TODO register
    }

    @Override
    public void updatePassword(SysUser sysUser) {
        SysUserDo u = sysUserMapper.selectById(sysUser.getUserId());
        u.setPassword(sysUser.getPassword());
        entryptPassword(u);
        u.setUpdateTime(LocalDateTime.now());
        sysUserMapper.updateById(u);
    }

    @Override
    public SysUser findByUsername(String username) {
        try {
            SysUser user = SysUser.builder().username(username).build();
            List<SysUser> list = sysUserMapper.findLoginUser(user);
            if (CollUtil.isNotEmpty(list)) {
                return list.iterator().next();
            }
            return null;
        } catch (Exception e) {
            log.error("# 根据账号查询用户报错 , username={}", username);
            throw new BusinessException("1001", "查询用户失败");
        }
    }

    @Override
    public SysUser findByMobile(String mobile) {
        try {
            SysUser user = SysUser.builder().mobile(mobile).build();
            List<SysUser> list = sysUserMapper.findLoginUser(user);
            if (CollUtil.isNotEmpty(list)) {
                return list.iterator().next();
            }
            return null;
        } catch (Exception e) {
            log.error("# 根据账号查询用户报错 , mobile={}", mobile);
            throw new BusinessException("1001", "查询用户失败");
        }
    }

    @Override
    public SysUser findByOpenid(String openid) {
        try {
            SysUser user = SysUser.builder().openid(openid).build();
            List<SysUser> list = sysUserMapper.findLoginUser(user);
            if (CollUtil.isNotEmpty(list)) {
                return list.iterator().next();
            }
            return null;
        } catch (Exception e) {
            log.error("# 根据账号查询用户报错 , openid={}", openid);
            throw new BusinessException("1001", "查询用户失败");
        }
    }

    @Transactional
    @Override
    public void updateLastLoginTime(SysUser sysUser) {
        if (null == sysUser || null == sysUser.getUserId()) {
            return;
        }
        SysUserDo u = sysUserMapper.selectById(sysUser.getUserId());
        if (u != null) {
            LocalDateTime lastLoginTime = u.getLastLoginTime();
            if (lastLoginTime == null) {
                lastLoginTime = LocalDateTime.now();
            }
            SysUserDo sysUserDo = new SysUserDo();
            sysUserDo.setId(u.getId());
            sysUserDo.setLastLoginTime(lastLoginTime);
            sysUserMapper.updateById(sysUserDo);
        }
    }

    @Override
    public PageInfo<SysUser> findSysUserByPage(Integer pageNum, SysUser user) {
        log.info("findSysUserByPage , pageNum={},{}", pageNum, user);
        pageNum = pageNum == null ? 1 : pageNum;
        PageHelper.startPage(pageNum, DemoConstants.PAGE_SIZE);
        List<SysUser> list = sysUserMapper.findUser(user);
        PageInfo<SysUser> page = new PageInfo<>(list);
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
        SysUserDo userDo = sysUserMapper.selectById(userId);
        if (null == userDo) {
            return WrapperResp.validateFailed("删除失败，用户不存在！");
        }

        List<SysUserRoleDo> list = sysUserRoleMapper.findUserRoleByUserId(userId);
        if (CollUtil.isNotEmpty(list)) {
            int ret = sysUserRoleMapper.delete(new LambdaUpdateWrapper<SysUserRoleDo>().eq(SysUserRoleDo::getUserId, userId));
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
    public WrapperResp<Boolean> editUser(SysUser user) {
        log.info("editUser, {}", user);
        if (ObjectUtil.isEmpty(user) || StrUtil.isBlank(user.getUserId())) {
            return WrapperResp.validateFailed("编辑保存失败，缺失用户信息！");
        }
        if (StrUtil.isAllBlank(user.getRoleId(), user.getEmail())) {
            return WrapperResp.validateFailed("编辑保存失败，用户角色必选，用户邮箱必填写！");
        }
        if (ObjectUtil.isEmpty(user.getStatus())) {
            return WrapperResp.validateFailed("编辑保存失败，用户状态未指定！");
        }
        SysUserDo userDo = sysUserMapper.selectById(user.getUserId());
        if (null == userDo) {
            return WrapperResp.validateFailed("编辑保存失败，用户不存在！");
        }

        LocalDateTime now = LocalDateTime.now();
        String operator = Identify.getSessionUser().getUserId();
        // 更新用户状态、邮箱
        int updateResult = sysUserMapper.update(
                new LambdaUpdateWrapper<SysUserDo>()
                        .eq(SysUserDo::getId, user.getUserId())
                        .set(SysUserDo::getStatus, user.getStatus())
                        .set(SysUserDo::getEmail, user.getEmail())
                        .set(SysUserDo::getUpdateTime, now, "typeHandler=" + CustomDateTypeHandler.class.getName())
                        .set(SysUserDo::getUpdateBy, operator)
        );
        if (updateResult == 0) {
            log.error("editUser, sysUserMapper.update fail, {},now={},operator={}", user, now, operator);
            return WrapperResp.failed("编辑保存失败！");
        }
        List<SysUserRoleDo> list = sysUserRoleMapper.findUserRoleByUserId(user.getUserId());
        if (CollUtil.isEmpty(list)) {
            // 用户找不到角色，给用户新增角色
            SysUserRoleDo userRoleDo = SysUserRoleDo.builder()
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
            SysUserRoleDo userRoleDo = list.get(0);
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
    public WrapperResp<Boolean> resetPwd(SysUser user) {
        log.info("resetPwd, {}", user);
        if (ObjectUtil.isEmpty(user) || StrUtil.isBlank(user.getUserId())) {
            return WrapperResp.validateFailed("重置密码失败，缺失用户信息！");
        }
        if (StrUtil.isBlank(user.getNewPassword())) {
            return WrapperResp.validateFailed("请输入密码！");
        }
        SysUserDo userDo = sysUserMapper.selectById(user.getUserId());
        if (null == userDo) {
            return WrapperResp.validateFailed("重置密码失败，用户不存在！");
        }
        String password = SecurityUtils.encryptPassword(userDo.getSalt(), user.getNewPassword(), userDo.getUsername());
        Date now = Calendar.getInstance().getTime();
        String operator = Identify.getSessionUser().getUserId();
        // 更新用户状态、邮箱
        int updateResult = sysUserMapper.update(
                new LambdaUpdateWrapper<SysUserDo>()
                        .eq(SysUserDo::getId, user.getUserId())
                        .set(SysUserDo::getPassword, password)
                        .set(SysUserDo::getUpdateTime, now, "typeHandler=" + CustomDateTypeHandler.class.getName())
                        .set(SysUserDo::getUpdateBy, operator)
        );
        if (updateResult == 0) {
            log.error("resetPwd, sysUserMapper.update fail, {},now={},operator={}", user, now, operator);
            return WrapperResp.failed("重置密码失败！");
        }
        return WrapperResp.success(true);
    }

    @Override
    public List<SysUser> findAll(SysUser user) {
        return sysUserMapper.findUser(user);
    }
}
