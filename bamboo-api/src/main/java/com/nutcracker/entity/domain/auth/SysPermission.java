package com.nutcracker.entity.domain.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 权限vo
 *
 * @author 胡桃夹子
 * @date 2025/01/02 10:43:46
 */
@Data
public class SysPermission implements Serializable {

    private static final long serialVersionUID = -2051933842290600230L;

    @Schema(description = "路由ID")
    private String id;

    @Schema(description = "父级路由ID，根节点为 NULL")
    private String parentId;

    @Schema(description = "路由路径", example = "/home/index")
    private String path;

    @Schema(description = "路由名称，唯一，用于编程式导航", example = "home")
    private String name;

    @Schema(description = "组件路径（懒加载）'", example = "'/home/index")
    private String component;

    @Schema(description = "重定向路径")
    private String redirect;

    @Schema(description = "菜单图标（可选默认值）", example = "HomeFilled")
    private String icon;

    @Schema(description = "菜单/页面标题", example = "首页")
    private String title;

    @Schema(description = "外部链接地址，为空表示内部路由")
    private String isLink;

    @Schema(description = "是否隐藏菜单项（0=显示，1=隐藏）", example = "0")
    private Boolean isHide;

    @Schema(description = "是否全屏显示（如登录页）", example = "0")
    private Boolean isFull;

    @Schema(description = "是否固定标签（常驻 tab）", example = "1")
    private Boolean isAffix;

    @Schema(description = "是否缓存组件（keep-alive）", example = "1")
    private Boolean isKeepAlive;

    @Schema(description = "高亮菜单路径（用于详情页返回父级）")
    private String activeMenu;

    @Schema(description = "是否不继承父级 affix 属性（扩展字段）")
    private Boolean noAffixParent;

    @Schema(description = "排序权重，值越小越靠前")
    private Integer sortOrder;

    @Schema(description = "逻辑删除：0=未删除，1=已删除", example = "0")
    private Boolean isDeleted;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "是否选中,1=选中,0=未选择,3=部分选中")
    private Integer checked;

}
