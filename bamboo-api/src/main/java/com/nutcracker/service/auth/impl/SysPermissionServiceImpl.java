package com.nutcracker.service.auth.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.nutcracker.common.wrapper.WrapperResp;
import com.nutcracker.constant.CacheableKey;
import com.nutcracker.entity.convert.auth.SysPermissionConvert;
import com.nutcracker.entity.dataobject.auth.SysPermissionDo;
import com.nutcracker.entity.domain.auth.SysPermission;
import com.nutcracker.entity.vo.auth.RouteRecordRawVo;
import com.nutcracker.mapper.auth.SysPermissionMapper;
import com.nutcracker.mapper.auth.SysRolePermissionMapper;
import com.nutcracker.service.auth.SysPermissionService;
import com.nutcracker.util.JSON;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 权限服务
 *
 * @author 胡桃夹子
 * @date 2025/01/02 15:15:00
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class SysPermissionServiceImpl implements SysPermissionService {

    private final SysPermissionMapper sysPermissionMapper;
    private final SysRolePermissionMapper sysRolePermissionMapper;

    @Override
    public List<RouteRecordRawVo> findSysPermission() {
        List<SysPermissionDo> permissionDoList = sysPermissionMapper.findAll();
        log.info("findAllSysPermission: \n{}", JSON.toJSONString(permissionDoList));
        if (CollUtil.isEmpty(permissionDoList)) {
            return Collections.emptyList();
        }
        List<SysPermission> list = SysPermissionConvert.INSTANCE.toDomain(permissionDoList);
        return getPermissionTree(list);
    }

    @Override
    //@Cacheable(cacheNames = CacheableKey.ROLE_PERMISSION, key = "#roleId", condition = "#roleId != null", unless = "#result == null")
    public List<RouteRecordRawVo> getRolePermissionByRoleId(String roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("roleId cannot be null");
        }
        log.info("getRolePermissionByRoleId roleId={}", roleId);
        List<SysPermissionDo> permissionDoList = sysPermissionMapper.getSysPermissionByRoleId(roleId);
        log.info("getRolePermissionByRoleId, roleId={},permissionDoList.size={}", roleId, CollUtil.size(permissionDoList));
        if (CollUtil.isEmpty(permissionDoList)) {
            return Collections.emptyList();
        }
        List<SysPermission> list = SysPermissionConvert.INSTANCE.toDomain(permissionDoList);
        List<RouteRecordRawVo> result = getPermissionTree(list);
        log.debug("getRolePermissionByRoleId roleId={},result.size={}", roleId, CollUtil.size(result));
        return result;
    }

    @Override
    public SysPermission getPermission(String id) {
        log.info("getPermission id={}", id);
        SysPermissionDo permissionDo = sysPermissionMapper.selectById(id);
        SysPermission permission = SysPermissionConvert.INSTANCE.toDomain(permissionDo);
        log.info("getPermission id={},permission={}", id, JSON.toJSONString(permission));
        return permission;
    }

    private List<RouteRecordRawVo> getPermissionTree(List<SysPermission> permissionList) {
        // 如果查询结果为空，直接返回空列表
        if (CollectionUtil.isEmpty(permissionList)) {
            return Collections.emptyList();
        }

        // 构建ID到VO的映射
        Map<String, RouteRecordRawVo> voMap = new HashMap<>(permissionList.size());

        // 转换所有路由为VO对象
        for (SysPermission router : permissionList) {
            // 构建元数据
            RouteRecordRawVo.MetaVo metaVo = RouteRecordRawVo.MetaVo.builder()
                    .icon(router.getIcon())
                    .title(router.getTitle())
                    .activeMenu(router.getActiveMenu())
                    .isLink(router.getIsLink())
                    .isHide(router.getIsHide())
                    .isFull(router.getIsFull())
                    .isAffix(router.getIsAffix())
                    .isKeepAlive(router.getIsKeepAlive())
                    .build();

            // 构建路由对象
            RouteRecordRawVo vo = RouteRecordRawVo.builder()
                    .path(router.getPath())
                    .name(router.getName())
                    .redirect(router.getRedirect())
                    .component(router.getComponent())
                    .meta(metaVo)
                    .children(new ArrayList<>())
                    .build();
            voMap.put(router.getId(), vo);
        }

        // 构建树形结构
        List<RouteRecordRawVo> rootList = new ArrayList<>();
        for (SysPermission router : permissionList) {
            RouteRecordRawVo currentVo = voMap.get(router.getId());
            if (router.getParentId() == null) {
                rootList.add(currentVo);
            } else {
                RouteRecordRawVo parentVo = voMap.get(router.getParentId());
                if (parentVo != null) {
                    parentVo.getChildren().add(currentVo);
                }
            }
        }
        return rootList;
    }

    @CacheEvict(cacheNames = CacheableKey.ROLE_PERMISSION, allEntries = true)
    @Transactional
    @Override
    public WrapperResp<Boolean> savePermission(SysPermission permission) {
        log.info("savePermission {}", permission);
        //if (permission == null || StrUtil.isBlank(permission.getPermissionCode()) || StrUtil.isBlank(permission.getPermissionName())) {
        //    log.error("savePermission fail, {}", permission);
        //    return WrapperResp.validateFailed("保存失败，缺少必要参数");
        //}
        //
        //SysPermissionDo permissionDo = sysPermissionMapper.findByPermissionCode(permission.getPermissionCode());
        //int resultNum;
        //if (StrUtil.isNotBlank(permission.getId())) {
        //    // 更新
        //    if (null != permissionDo && !StrUtil.equals(permission.getId(), permissionDo.getId()) && StrUtil.equals(permission.getPermissionCode(), permissionDo.getPermissionCode())) {
        //        return WrapperResp.validateFailed("更新失败，权限编码已存在");
        //    }
        //    SysPermissionDo p = sysPermissionMapper.selectById(permission.getId());
        //    if (null == p) {
        //        return WrapperResp.validateFailed("更新失败，菜单信息不存在！");
        //    }
        //    String oldPermissionCode = p.getPermissionCode();
        //    p = SysPermissionConvert.INSTANCE.toDo(permission);
        //    resultNum = sysPermissionMapper.updateSysPermissionById(p);
        //
        //    // 菜单变更
        //    if(!StrUtil.equals(permission.getPermissionCode(), oldPermissionCode)){
        //        // 更新子菜单数据
        //        sysPermissionMapper.updateParentPermissionCode(permission.getPermissionCode(), oldPermissionCode);
        //    }
        //} else {
        //    // 新增
        //    if (null != permissionDo && StrUtil.equals(permission.getPermissionCode(), permissionDo.getPermissionCode())) {
        //        return WrapperResp.validateFailed("保存失败，权限编码已存在");
        //    }
        //    SysPermissionDo p = SysPermissionConvert.INSTANCE.toDo(permission);
        //    p.setId(String.valueOf(IdWorker.getId("t_sys_permission")));
        //    p.setCreateTime(LocalDateTime.now());
        //    p.setCreateBy(Identify.getSessionUser().getUserId());
        //    resultNum = sysPermissionMapper.insert(p);
        //}
        //log.info("savePermission {},resultNum={}", permission, resultNum);
        //if (resultNum == 0) {
        //    return WrapperResp.failed("保存失败，缺少必要参数");
        //}
        return WrapperResp.success(Boolean.TRUE);
    }

    @Override
    @CacheEvict(cacheNames = CacheableKey.ROLE_PERMISSION, allEntries = true)
    public WrapperResp<Boolean> deletePermission(String permissionId) {
        log.info("deletePermission permissionId={}", permissionId);
        //SysPermissionDo sysPermissionDo = sysPermissionMapper.selectById(permissionId);
        //List<SysPermissionDo> children = sysPermissionMapper.findByParentPermissionCode(sysPermissionDo.getPermissionCode());
        //if (CollUtil.isNotEmpty(children)) {
        //    return WrapperResp.validateFailed("因为还有下级菜单，无法执行删除操作！");
        //}
        //List<SysRolePermissionDo> list = sysRolePermissionMapper.selectList(
        //        new LambdaQueryWrapper<SysRolePermissionDo>()
        //                .eq(SysRolePermissionDo::getPermissionId, permissionId)
        //);
        //if (CollUtil.isNotEmpty(list)) {
        //    int ret = sysRolePermissionMapper.delete(
        //            new LambdaUpdateWrapper<SysRolePermissionDo>()
        //                    .eq(SysRolePermissionDo::getPermissionId, permissionId)
        //    );
        //    if (ret == 0) {
        //        log.error("resetPwd, sysRolePermissionMapper.delete fail, permissionId={}", permissionId);
        //        return WrapperResp.failed("删除失败！");
        //    }
        //}
        //if (sysPermissionMapper.deleteById(permissionId) == 0) {
        //    log.error("resetPwd, sysPermissionMapper.deleteById fail, permissionId={}", permissionId);
        //    return WrapperResp.failed("删除失败！");
        //}
        return WrapperResp.success(Boolean.TRUE);
    }

    @Override
    public List<RouteRecordRawVo> getSysPermissionByRoleId(String roleId) {
        List<SysPermission> sysPermissionDos = sysPermissionMapper.findAllByRoleId(roleId);
        if (CollUtil.isNotEmpty(sysPermissionDos)) {
            List<RouteRecordRawVo> list = getPermissionTree(sysPermissionDos);

        }
        return Collections.emptyList();
    }
}
