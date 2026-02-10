package com.nutcracker.bamboo.domain.model.entity;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 权限vo
 *
 * @author 胡桃夹子
 * @date 2025/01/02 10:43:46
 */
@Data
public class Permission implements Serializable {

    @Serial
    private static final long serialVersionUID = -2051933842290600230L;

    
    private String id;

    
    private String parentId;

    @Schema(description = "路由路径", example = "/home/index")
    private String path;

    @Schema(description = "路由名称，唯一，用于编程式导航", example = "home")
    private String name;

    @Schema(description = "组件路径（懒加载）'", example = "'/home/index")
    private String component;

    
    private String redirect;

    @Schema(description = "菜单图标（可选默认值）", example = "HomeFilled")
    private String icon;

    @Schema(description = "菜单/页面标题", example = "首页")
    private String title;

    
    private String isLink;

    @Schema(description = "是否隐藏菜单项（0=显示，1=隐藏）", example = "0")
    private Boolean isHide;

    @Schema(description = "是否全屏显示（如登录页）", example = "0")
    private Boolean isFull;

    @Schema(description = "是否固定标签（常驻 tab）", example = "1")
    private Boolean isAffix;

    @Schema(description = "是否缓存组件（keep-alive）", example = "1")
    private Boolean isKeepAlive;

    
    private String activeMenu;

    
    private Boolean noAffixParent;

    
    private Integer sortOrder;

    @Schema(description = "逻辑删除：0=未删除，1=已删除", example = "0")
    private Boolean isDeleted;

    
    private LocalDateTime createTime;

    
    private LocalDateTime updateTime;

    
    private Integer checked;

}
