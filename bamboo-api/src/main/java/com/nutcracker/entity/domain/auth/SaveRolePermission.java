package com.nutcracker.entity.domain.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 角色权限保存
 *
 * @author 胡桃夹子
 * @date 2025/03/27 15:39:57
 */
@Data
public class SaveRolePermission implements Serializable {

    private static final long serialVersionUID = -5302468045494660387L;

    @Schema(description = "角色ID")
    private String roleId;

    @Schema(description = "菜单资源ID集合")
    private List<String> permissionIdList;

}
