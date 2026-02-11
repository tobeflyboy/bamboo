package com.nutcracker.bamboo.application.service.auth.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.nutcracker.bamboo.application.model.response.RouteRecordRawVo;
import com.nutcracker.bamboo.application.service.auth.PermissionService;
import com.nutcracker.bamboo.common.constant.CacheableKey;
import com.nutcracker.bamboo.common.constant.PrimaryKey;
import com.nutcracker.bamboo.common.util.JSON;
import com.nutcracker.bamboo.common.wrapper.WrapperResp;
import com.nutcracker.bamboo.domain.model.entity.Permission;
import com.nutcracker.bamboo.infrastructure.converter.auth.PermissionConvert;
import com.nutcracker.bamboo.infrastructure.entity.auth.PermissionDo;
import com.nutcracker.bamboo.infrastructure.entity.auth.RolePermissionDo;
import com.nutcracker.bamboo.infrastructure.mapper.auth.PermissionMapper;
import com.nutcracker.bamboo.infrastructure.mapper.auth.RolePermissionMapper;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 权限服务
 *
 * @author 胡桃夹子
 * @date 2025/01/02 15:15:00
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class PermissionServiceImpl implements PermissionService {

    private final PermissionMapper permissionMapper;
    private final RolePermissionMapper rolePermissionMapper;

    @Override
    public List<RouteRecordRawVo> findPermission() {
        List<PermissionDo> permissionDoList = permissionMapper.findAll();
        log.info("findAllPermission: \n{}", JSON.toJSONString(permissionDoList));
        if (CollUtil.isEmpty(permissionDoList)) {
            return Collections.emptyList();
        }
        List<Permission> list = PermissionConvert.INSTANCE.toDomain(permissionDoList);
        return getPermissionTree(list, false);
    }

    @Override
    //@Cacheable(cacheNames = CacheableKey.ROLE_PERMISSION, key = "#roleId", condition = "#roleId != null", unless = "#result == null")
    public List<RouteRecordRawVo> getRolePermissionByRoleId(String roleId) {
        if (roleId == null) {
            throw new IllegalArgumentException("roleId cannot be null");
        }
        log.info("getRolePermissionByRoleId roleId={}", roleId);
        List<PermissionDo> permissionDoList = permissionMapper.getPermissionByRoleId(roleId);
        log.info("getRolePermissionByRoleId, roleId={},permissionDoList.size={}", roleId, CollUtil.size(permissionDoList));
        if (CollUtil.isEmpty(permissionDoList)) {
            return Collections.emptyList();
        }
        List<Permission> list = PermissionConvert.INSTANCE.toDomain(permissionDoList);
        List<RouteRecordRawVo> result = getPermissionTree(list, true);
        log.debug("getRolePermissionByRoleId roleId={},result.size={}", roleId, CollUtil.size(result));
        return result;
    }

    @Override
    public Permission getPermission(String id) {
        log.info("getPermission id={}", id);
        PermissionDo permissionDo = permissionMapper.selectById(id);
        Permission permission = PermissionConvert.INSTANCE.toDomain(permissionDo);
        log.info("getPermission id={},permission={}", id, JSON.toJSONString(permission));
        return permission;
    }

    private List<RouteRecordRawVo> getPermissionTree(List<Permission> permissionList, boolean checked) {
        if (CollectionUtil.isEmpty(permissionList)) {
            return Collections.emptyList();
        }

        // 1. 先构建所有节点的 VO
        Map<String, RouteRecordRawVo> voMap = permissionList.stream().collect(Collectors.toMap(
                Permission::getId,
                router -> {
                    RouteRecordRawVo.MetaVo meta = RouteRecordRawVo.MetaVo.builder()
                            .icon(router.getIcon())
                            .title(router.getTitle())
                            .activeMenu(router.getActiveMenu())
                            .isLink(router.getIsLink())
                            .isHide(router.getIsHide())
                            .isFull(router.getIsFull())
                            .isAffix(router.getIsAffix())
                            .isKeepAlive(router.getIsKeepAlive())
                            .build();
                    return RouteRecordRawVo.builder()
                            .id(router.getId())
                            .parentId(router.getParentId())
                            .path(router.getPath())
                            .name(router.getName())
                            .redirect(router.getRedirect())
                            .component(router.getComponent())
                            .meta(meta)
                            .sortOrder(router.getSortOrder())
                            .checked(Optional.ofNullable(router.getChecked()).orElse(0))
                            .children(new ArrayList<>())
                            .build();
                }
        ));

        // 2. 构建树结构（并计算子节点选中数量）
        List<RouteRecordRawVo> rootList = new ArrayList<>();
        voMap.values().forEach(vo -> {
            String parentId = vo.getParentId();
            if (parentId == null) {
                rootList.add(vo);
            } else {
                RouteRecordRawVo parent = voMap.get(parentId);
                if (parent != null) {
                    parent.getChildren().add(vo);
                }
            }
        });

        if (checked) {
            // 3. 自底向上计算“部分选中(3)”状态
            rootList.forEach(this::updateCheckedState);
        }

        return rootList;
    }

    /**
     * 递归计算 checked 状态（返回当前节点的选中子节点数量）
     */
    private int updateCheckedState(RouteRecordRawVo node) {
        List<RouteRecordRawVo> children = node.getChildren();
        if (CollectionUtil.isEmpty(children)) {
            // 叶子节点：返回自身是否选中
            return node.getChecked() == 1 ? 1 : 0;
        }

        int selectedChildren = 0;
        for (RouteRecordRawVo child : children) {
            selectedChildren += updateCheckedState(child);
        }

        int totalChildren = children.size();
        node.setChildrenCheckedNum(selectedChildren);

        if (selectedChildren == 0) {
            // 子节点都没选
            node.setChecked(0);
        } else if (selectedChildren == totalChildren) {
            // 子节点都选中
            node.setChecked(1);
        } else {
            // 部分选中
            node.setChecked(3);
        }
        return node.getChecked() == 1 ? 1 : 0;
    }


    @CacheEvict(cacheNames = CacheableKey.ROLE_PERMISSION, allEntries = true)
    @Transactional
    @Override
    public WrapperResp<Boolean> savePermission(Permission permission) {
        log.info("savePermission {}", permission);
        if (permission == null
                || StrUtil.isBlank(permission.getName())
                || StrUtil.isBlank(permission.getIcon())
                || StrUtil.isBlank(permission.getTitle())) {
            log.error("savePermission fail, {}", permission);
            return WrapperResp.validateFailed("保存失败，缺少必要参数");
        }
        if (StrUtil.isEmpty(permission.getParentId())) {
            permission.setParentId(null);
        }
        if (StrUtil.isEmpty(permission.getRedirect())) {
            permission.setRedirect(null);
        }
        int resultNum;
        if (StrUtil.isNotBlank(permission.getId())) {
            PermissionDo p = permissionMapper.selectById(permission.getId());
            if (null == p) {
                return WrapperResp.validateFailed("更新失败，菜单信息不存在！");
            }
            p = PermissionConvert.INSTANCE.toDo(permission);
            resultNum = permissionMapper.updateById(p);
        } else {
            // 判断是不是已经存在相同的菜单
            LambdaQueryWrapper<PermissionDo> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(PermissionDo::getName, permission.getName())
                       .eq(PermissionDo::getPath, permission.getPath())
                       .eq(PermissionDo::getIsDeleted, false);
            
            Long count = permissionMapper.selectCount(queryWrapper);
            if (count > 0) {
                return WrapperResp.validateFailed("菜单已存在，不能重复添加！");
            }
            
            // 新增
            PermissionDo p = PermissionConvert.INSTANCE.toDo(permission);
            p.setId(PrimaryKey.getPermissionId());
            p.setCreateTime(LocalDateTime.now());
            resultNum = permissionMapper.insert(p);
        }
        log.info("savePermission {},resultNum={}", permission, resultNum);
        if (resultNum == 0) {
            return WrapperResp.failed("保存失败，缺少必要参数");
        }
        return WrapperResp.success(Boolean.TRUE);
    }

    @Override
    @CacheEvict(cacheNames = CacheableKey.ROLE_PERMISSION, allEntries = true)
    public WrapperResp<Boolean> deletePermission(String permissionId) {
        log.info("deletePermission permissionId={}", permissionId);
        PermissionDo sysPermissionDo = permissionMapper.selectById(permissionId);
        if (null == sysPermissionDo) {
            log.warn("找不到菜单，删除失败！permissionId={}", permissionId);
            return WrapperResp.validateFailed("找不到菜单，删除失败！");
        }
        List<PermissionDo> children = permissionMapper.findByParentPermissionId(permissionId);
        if (CollUtil.isNotEmpty(children)) {
            log.warn("因为还有下级菜单，无法执行删除操作！permissionId={}", permissionId);
            return WrapperResp.validateFailed("因为还有下级菜单，无法执行删除操作！");
        }

        List<RolePermissionDo> list = rolePermissionMapper.selectList(
                new LambdaQueryWrapper<RolePermissionDo>()
                        .eq(RolePermissionDo::getPermissionId, permissionId)
        );
        if (CollUtil.isNotEmpty(list)) {
            // 删除角色菜单授权
            int ret = rolePermissionMapper.delete(
                    new LambdaUpdateWrapper<RolePermissionDo>()
                            .eq(RolePermissionDo::getPermissionId, permissionId)
            );
            if (ret != list.size()) {
                log.error("resetPwd, sysRolePermissionMapper.delete fail, permissionId={}", permissionId);
                return WrapperResp.failed("删除失败！");
            }
        }
        sysPermissionDo.setIsDeleted(true);
        sysPermissionDo.setUpdateTime(LocalDateTime.now());
        // 实际上是逻辑删除
        if (permissionMapper.deleteById(sysPermissionDo) == 0) {
            log.error("resetPwd, sysPermissionMapper.deleteById fail, permissionId={}", permissionId);
            return WrapperResp.failed("删除失败！");
        }
        return WrapperResp.success(Boolean.TRUE);
    }

    @Override
    public List<RouteRecordRawVo> getPermissionByRoleId(String roleId) {
        List<Permission> sysPermissionDos = permissionMapper.findAllByRoleId(roleId);
        List<RouteRecordRawVo> list;
        if (CollUtil.isNotEmpty(sysPermissionDos)) {
            list = getPermissionTree(sysPermissionDos, true);
        } else {
            list = Collections.emptyList();
        }
        return list;
    }
}
