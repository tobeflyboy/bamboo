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
import com.nutcracker.bamboo.application.dto.auth.RouteRecordRawVo;
import com.nutcracker.bamboo.application.service.auth.SysPermissionService;
import com.nutcracker.bamboo.domain.auth.model.SysPermission;
import com.nutcracker.bamboo.infrastructure.persistence.entity.auth.SysPermissionDo;
import com.nutcracker.bamboo.infrastructure.persistence.entity.auth.SysRolePermissionDo;
import com.nutcracker.bamboo.infrastructure.persistence.mapper.auth.SysPermissionMapper;
import com.nutcracker.bamboo.infrastructure.persistence.mapper.auth.SysRolePermissionMapper;
import com.nutcracker.bamboo.infrastructure.persistence.repository.auth.SysPermissionConvert;
import com.nutcracker.shared.common.wrapper.WrapperResp;
import com.nutcracker.shared.constant.CacheableKey;
import com.nutcracker.shared.constant.PrimaryKey;
import com.nutcracker.shared.util.JSON;

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
        return getPermissionTree(list, false);
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
        List<RouteRecordRawVo> result = getPermissionTree(list, true);
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

    private List<RouteRecordRawVo> getPermissionTree(List<SysPermission> permissionList, boolean checked) {
        if (CollectionUtil.isEmpty(permissionList)) {
            return Collections.emptyList();
        }

        // 1. 先构建所有节点的 VO
        Map<String, RouteRecordRawVo> voMap = permissionList.stream().collect(Collectors.toMap(
                SysPermission::getId,
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
    public WrapperResp<Boolean> savePermission(SysPermission permission) {
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
            SysPermissionDo p = sysPermissionMapper.selectById(permission.getId());
            if (null == p) {
                return WrapperResp.validateFailed("更新失败，菜单信息不存在！");
            }
            p = SysPermissionConvert.INSTANCE.toDo(permission);
            resultNum = sysPermissionMapper.updateById(p);
        } else {
            // TODO 判断是不是已经存在相同的菜单
            // 新增
            SysPermissionDo p = SysPermissionConvert.INSTANCE.toDo(permission);
            p.setId(PrimaryKey.getSysPermissionId());
            p.setCreateTime(LocalDateTime.now());
            resultNum = sysPermissionMapper.insert(p);
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
        SysPermissionDo sysPermissionDo = sysPermissionMapper.selectById(permissionId);
        if (null == sysPermissionDo) {
            log.warn("找不到菜单，删除失败！permissionId={}", permissionId);
            return WrapperResp.validateFailed("找不到菜单，删除失败！");
        }
        List<SysPermissionDo> children = sysPermissionMapper.findByParentPermissionId(permissionId);
        if (CollUtil.isNotEmpty(children)) {
            log.warn("因为还有下级菜单，无法执行删除操作！permissionId={}", permissionId);
            return WrapperResp.validateFailed("因为还有下级菜单，无法执行删除操作！");
        }

        List<SysRolePermissionDo> list = sysRolePermissionMapper.selectList(
                new LambdaQueryWrapper<SysRolePermissionDo>()
                        .eq(SysRolePermissionDo::getPermissionId, permissionId)
        );
        if (CollUtil.isNotEmpty(list)) {
            // 删除角色菜单授权
            int ret = sysRolePermissionMapper.delete(
                    new LambdaUpdateWrapper<SysRolePermissionDo>()
                            .eq(SysRolePermissionDo::getPermissionId, permissionId)
            );
            if (ret != list.size()) {
                log.error("resetPwd, sysRolePermissionMapper.delete fail, permissionId={}", permissionId);
                return WrapperResp.failed("删除失败！");
            }
        }
        sysPermissionDo.setIsDeleted(true);
        sysPermissionDo.setUpdateTime(LocalDateTime.now());
        // 实际上是逻辑删除
        if (sysPermissionMapper.deleteById(sysPermissionDo) == 0) {
            log.error("resetPwd, sysPermissionMapper.deleteById fail, permissionId={}", permissionId);
            return WrapperResp.failed("删除失败！");
        }
        return WrapperResp.success(Boolean.TRUE);
    }

    @Override
    public List<RouteRecordRawVo> getSysPermissionByRoleId(String roleId) {
        List<SysPermission> sysPermissionDos = sysPermissionMapper.findAllByRoleId(roleId);
        List<RouteRecordRawVo> list;
        if (CollUtil.isNotEmpty(sysPermissionDos)) {
            list = getPermissionTree(sysPermissionDos, true);
        } else {
            list = Collections.emptyList();
        }
        return list;
    }
}
