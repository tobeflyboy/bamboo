package com.nutcracker.web.rest;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelReader;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.github.pagehelper.PageInfo;
import com.nutcracker.common.enums.SysUserStatusEnum;
import com.nutcracker.common.wrapper.WrapperResp;
import com.nutcracker.entity.domain.auth.SaveRolePermission;
import com.nutcracker.entity.domain.auth.SysPermission;
import com.nutcracker.entity.domain.auth.SysRole;
import com.nutcracker.entity.domain.auth.SysUser;
import com.nutcracker.entity.vo.auth.RouteRecordRawVo;
import com.nutcracker.service.auth.SysPermissionService;
import com.nutcracker.service.auth.SysRoleService;
import com.nutcracker.service.auth.SysUserService;
import com.nutcracker.util.JSON;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * auth-api控制器
 *
 * @author 胡桃夹子
 * @date 2025/04/14 11:11:58
 */
@Tag(name = "2.权限管理", description = "有关资源菜单、角色、用户想着的接口")
@RequiredArgsConstructor
@Slf4j
@RestController
public class AuthManagerRestController {

    private final SysPermissionService sysPermissionService;
    private final SysRoleService sysRoleService;
    private final SysUserService sysUserService;

    @Operation(summary = "【菜单】菜单树接口", description = "菜单树接口")
    @PostMapping("/api/permission/tree")
    public WrapperResp<List<RouteRecordRawVo>> permissionTree() {
        log.info("/api/permission/tree");
        List<RouteRecordRawVo> list = sysPermissionService.findSysPermission();
        WrapperResp<List<RouteRecordRawVo>> resp = WrapperResp.success(list);
        log.info("/api/permission/tree,{}", JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【菜单】菜单详情接口", description = "菜单详情接口")
    @PostMapping("/api/permission/{permissionId}")
    public WrapperResp<SysPermission> permission(@PathVariable("permissionId") String permissionId) {
        log.info(" /api/permission/{}", permissionId);
        SysPermission permission = sysPermissionService.getPermission(permissionId);
        WrapperResp<SysPermission> resp = WrapperResp.success(permission);
        log.info(" /api/permission/{},{}", permissionId, JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【菜单】菜单新增或菜单更新", description = "菜单新增或菜单更新接口")
    @PostMapping("/api/permission/save")
    public WrapperResp<Boolean> permissionSave(@RequestBody SysPermission sysPermission) {
        log.info("/api/permission/save {}", sysPermission);
        WrapperResp<Boolean> response = sysPermissionService.savePermission(sysPermission);
        log.info("/api/permission/save {}, response={}", sysPermission, response);
        return response;
    }

    @Operation(summary = "【菜单】菜单删除接口", description = "菜单删除接口")
    @PostMapping("/api/permission/delete/{permissionId}")
    public WrapperResp<Boolean> permissionDelete(@PathVariable("permissionId") String permissionId) {
        log.info("/api/permission/delete permissionId={}", permissionId);
        WrapperResp<Boolean> response = sysPermissionService.deletePermission(permissionId);
        log.info("/api/permission/delete permissionId={}, response={}", permissionId, response);
        return response;
    }

    @Operation(summary = "【角色】查询所有角色接口", description = "角色查询所有有效数据接口")
    @PostMapping("/api/role/all-list")
    public WrapperResp<List<SysRole>> roleAllList() {
        log.info("/api/role/all-list");
        WrapperResp<List<SysRole>> resp = sysRoleService.roleList();
        log.info("/api/role/all-list {}", JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【角色】角色列表分页查询接口", description = "角色列表分页查询接口")
    @PostMapping("/api/role/list")
    public WrapperResp<PageInfo<SysRole>> roleList(
            @Parameter(name = "pageNum", description = "页码，当前第x页", example = "1")
            @RequestParam(value = "pageNum", required = false) Integer pageNum,
            @RequestBody SysRole role) {
        log.info("/api/role/list pageNum={},role={}", pageNum, JSON.toJSONString(role));
        PageInfo<SysRole> page = sysRoleService.findSysRoleByPage(pageNum, role);
        WrapperResp<PageInfo<SysRole>> resp = WrapperResp.success(page);
        log.info("/api/role/list pageNum={},role={},resp={}", pageNum, JSON.toJSONString(role), JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【角色】新增或更新接口", description = "角色新增或更新接口")
    @PostMapping("/api/role/save")
    public WrapperResp<Boolean> roleSave(@RequestBody SysRole role) {
        log.info("/api/role/save role={}", JSON.toJSONString(role));
        WrapperResp<Boolean> resp;
        if (null != role && StrUtil.isNotBlank(role.getId())) {
            resp = sysRoleService.editRole(role);
        } else {
            resp = sysRoleService.addRole(role);
        }
        log.info("/api/role/save role={}, resp={}", JSON.toJSONString(role), JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【角色】角色删除接口", description = "角色删除接口")
    @PostMapping("/api/role/delete/{roleId}")
    public WrapperResp<Boolean> deleteRole(@PathVariable("roleId") String roleId) {
        log.info("/api/role/delete/{}", roleId);
        WrapperResp<Boolean> resp = sysRoleService.deleteRole(roleId);
        log.info("/api/role/delete/{}, resp={}", roleId, JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【角色与菜单】查询接口", description = "根据角色查询对应的菜单查询接口")
    @PostMapping("/api/role_permission/{roleId}")
    public WrapperResp<List<RouteRecordRawVo>> rolePermission(@PathVariable("roleId") String roleId) {
        log.info("/api/role_permission/{}", roleId);
        List<RouteRecordRawVo> list = sysPermissionService.getSysPermissionByRoleId(roleId);
        WrapperResp<List<RouteRecordRawVo>> resp = WrapperResp.success(list);
        log.info("/api/role_permission/{},resp={}", roleId, JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【角色与菜单】授权保存接口", description = "角色与菜单，授权保存接口")
    @PostMapping("/api/role_permission/save")
    public WrapperResp<Boolean> rolePermissionSave(@RequestBody SaveRolePermission save) {
        log.info(" /api/role_permission/save {}", JSON.toJSONString(save));
        WrapperResp<Boolean> resp = sysRoleService.saveRolePermission(save);
        log.info(" /api/role_permission/save {}, resp={}", JSON.toJSONString(save), JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【用户】状态枚举接口", description = "用户状态枚举查询接口")
    @PostMapping("/api/user/status-enum")
    public WrapperResp<Map<Integer, String>> userStatusEnum() {
        Map<Integer, String> statusMap = new LinkedHashMap<>();
        Arrays.stream(SysUserStatusEnum.values()).forEach(e -> statusMap.put(e.getCode(), e.getMsg()));
        WrapperResp<Map<Integer, String>> resp = WrapperResp.success(statusMap);
        log.info("/api/user/status-enum,{}", JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【用户】列表分页查询接口", description = "用户列表分页查询接口")
    @PostMapping("/api/user/list")
    public WrapperResp<PageInfo<SysUser>> userList(
            @Parameter(name = "pageNum", description = "页码，当前第x页", example = "1")
            @RequestParam(name = "pageNum", required = false) Integer pageNum,
            @RequestBody(required = false) SysUser user) {
        log.info("/api/user/list pageNum={},{}", pageNum, JSON.toJSONString(user));
        PageInfo<SysUser> page = sysUserService.findSysUserByPage(pageNum, user);
        WrapperResp<PageInfo<SysUser>> resp = WrapperResp.success(page);
        log.info("/api/user/list pageNum={},{},{}", pageNum, JSON.toJSONString(user), JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【用户】新增接口", description = "用户新增接口")
    @PostMapping("/api/user/add")
    public WrapperResp<Boolean> userAdd(@RequestBody SysUser user) {
        log.info("/api/user/add {}", JSON.toJSONString(user));
        SysRole role = ObjectUtil.isEmpty(user) || ObjectUtil.isEmpty(user.getRoleId()) ? null : SysRole.builder().id(user.getRoleId()).build();
        WrapperResp<Boolean> resp = sysUserService.addSysUser(user, role);
        log.info("/api/user/add {},{}", JSON.toJSONString(user), JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【用户】编辑接口", description = "用户编辑接口")
    @PostMapping("/api/user/edit")
    public WrapperResp<Boolean> userEdit(@RequestBody SysUser user) {
        log.info("/api/user/edit {}", JSON.toJSONString(user));
        WrapperResp<Boolean> resp = sysUserService.editUser(user);
        log.info("/api/user/edit {}, resp={}", JSON.toJSONString(user), JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【用户】删除接口", description = "用户删除接口")
    @PostMapping("/api/user/delete/{userId}")
    public WrapperResp<Boolean> deleteUser(@PathVariable("userId") String userId) {
        log.info("/api/delete/user/{}", userId);
        WrapperResp<Boolean> resp = sysUserService.deleteUser(userId);
        log.info("/api/delete/user/{}, resp={}", userId, JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【用户】重置密码接口", description = "用户重置密码接口")
    @PostMapping("/api/user/reset-pwd")
    public WrapperResp<Boolean> userResetPwd(@RequestBody SysUser user) {
        log.info("/api/user/reset-pwd {}", JSON.toJSONString(user));
        WrapperResp<Boolean> resp = sysUserService.resetPwd(user);
        log.info("/api/user/reset-pwd {}, response={}", JSON.toJSONString(user), JSON.toJSONString(resp));
        return resp;
    }

    @Operation(summary = "【用户】Excel导出接口", description = "用户Excel导出接口")
    @GetMapping("/api/user/export")
    public void userExport(HttpServletResponse response, SysUser user) {
        try {
            log.info("/api/user/export, user={}", JSON.toJSONString(user));
            List<SysUser> list = sysUserService.findAll(user);
            if (CollUtil.isEmpty(list)) {
                throw new RuntimeException("没有可导出的数据");
            }
            // 使用临时缓冲区
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (ExcelWriter writer = ExcelUtil.getWriter(true)) {
                writer.addHeaderAlias("username", "账号");
                writer.addHeaderAlias("realName", "姓名");
                writer.addHeaderAlias("email", "邮箱");
                writer.addHeaderAlias("statusDesc", "状态");
                writer.addHeaderAlias("createTime", "创建时间");
                writer.addHeaderAlias("createUserRealName", "创建人");
                writer.addHeaderAlias("updateTime", "更新时间");
                writer.addHeaderAlias("updateUserRealName", "更新人");
                writer.addHeaderAlias("lastLoginTime", "最后登录时间");
                writer.addHeaderAlias("roleName", "角色");

                writer.setOnlyAlias(true);
                writer.write(list, true);
                writer.flush(outputStream);
            }
            String fileName = URLEncoder.encode("用户表格", StandardCharsets.UTF_8);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding(StandardCharsets.UTF_8.name());
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");
            response.setContentLength(outputStream.size());

            // 此处才真正写入 response 的输出流
            try (OutputStream os = response.getOutputStream()) {
                outputStream.writeTo(os);
            }
            log.info("用户信息导出成功");
        } catch (Exception e) {
            String json = JSON.toJSONString(WrapperResp.failed("导出失败：" + e.getMessage()));
            log.error("用户导出失败 resp={}", json, e);
            try {
                response.resetBuffer();
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.setContentType("application/json;charset=UTF-8");
                try (PrintWriter writer = response.getWriter()) {
                    writer.write(json);
                    writer.flush();
                }
            } catch (IOException ex) {
                log.error("写入错误信息失败", ex);
            }
        }
    }

    @Operation(summary = "【用户】Excel导入接口", description = "用户Excel导入接口")
    @PostMapping("/api/user/import")
    @SneakyThrows
    public WrapperResp<Boolean> userImport(@RequestParam("file") MultipartFile file) {
        log.info("api/user/import");
        InputStream stream = file.getInputStream();
        ExcelReader reader = ExcelUtil.getReader(stream);
        reader.addHeaderAlias("账号", "username");
        reader.addHeaderAlias("姓名", "realName");
        reader.addHeaderAlias("邮箱", "email");
        reader.addHeaderAlias("状态", "status");
        reader.addHeaderAlias("角色", "roleName");
        List<SysUser> list = reader.readAll(SysUser.class);
        // FIXME 这里只是展示如何接收前端上传的Excel数据，不做业务逻辑实现
        log.info("api/user/import, list={}", JSON.toJSONString(list));
        WrapperResp<Boolean> resp = WrapperResp.success(true);
        log.info("api/user/import, resp={}", JSON.toJSONString(resp));
        return resp;
    }

}
