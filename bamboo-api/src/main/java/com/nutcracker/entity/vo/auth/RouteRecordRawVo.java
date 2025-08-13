package com.nutcracker.entity.vo.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * 路由参数对象
 *
 * @author 胡桃夹子
 * @date 2025/08/07 11:01:44
 */
@Schema(description = "路由参数对象")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@Getter
@Setter
public class RouteRecordRawVo implements Serializable {
    private static final long serialVersionUID = -4690552821968239320L;

    @Schema(description = "路由访问路径", example = "/home/index")
    private String path;

    @Schema(description = "路由 name (对应页面组件 name, 可用作 KeepAlive 缓存标识 && 按钮权限筛选)", example = "home")
    private String name;

    @Schema(description = "路由重定向地址", example = "")
    private String redirect;

    @Schema(description = "视图文件路径", example = "/home/index")
    private String component;

    @Schema(description = "路由元信息")
    private MetaVo meta;

    @Schema(description = "多级路由嵌套")
    private List<RouteRecordRawVo> children;

    /**
     * 路由元信息
     *
     * @author 胡桃夹子
     * @date 2025/08/07 11:05:52
     */
    @Schema(description = "路由元信息")
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @ToString(callSuper = true)
    @Getter
    @Setter
    public static class MetaVo implements Serializable {
        private static final long serialVersionUID = -7298197283010048954L;

        @Schema(description = "菜单和面包屑对应的图标", example = "HomeFilled")
        private String icon;

        @Schema(description = "路由标题 (用作 document.title || 菜单的名称)", example = "首页")
        private String title;

        @Schema(description = "是否在菜单中隐藏, 需要高亮的 path (通常用作详情页高亮父级菜单)", example = "")
        private String activeMenu;

        @Schema(description = "路由外链时填写的访问地址", example = "")
        private String isLink;

        @Schema(description = "是否在菜单中隐藏 (通常列表详情页需要隐藏)", example = "false")
        private Boolean isHide;

        @Schema(description = "菜单是否全屏 (示例：数据大屏页面)", example = "false")
        private Boolean isFull;

        @Schema(description = "菜单是否固定在标签页中 (首页通常是固定项)", example = "true")
        private Boolean isAffix;

        @Schema(description = "当前路由是否缓存 ", example = "true")
        private Boolean isKeepAlive;
    }
}
