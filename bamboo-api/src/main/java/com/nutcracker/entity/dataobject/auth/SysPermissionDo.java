package com.nutcracker.entity.dataobject.auth;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * permission data object
 *
 * @author 胡桃夹子
 * @date 2025/01/02 10:41:32
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "资源表")
@TableName("sys_permission")
public class SysPermissionDo implements Serializable {

    private static final long serialVersionUID = -7141829387338999544L;

    /**
     * 路由ID
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 父级路由ID，根节点为 NULL
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 路由路径，如 '/proTable/useProTable'
     */
    @TableField("path")
    private String path;

    /**
     * 路由名称，唯一，用于编程式导航
     */
    @TableField("name")
    private String name;

    /**
     * 组件路径（懒加载），如 '/proTable/useProTable/index'
     */
    @TableField("component")
    private String component;

    /**
     * 重定向路径
     */
    @TableField("redirect")
    private String redirect;

    /**
     * 菜单图标（可选默认值）
     */
    @TableField("icon")
    private String icon;

    /**
     * 菜单/页面标题
     */
    @TableField("title")
    private String title;

    /**
     * 外部链接地址，为空表示内部路由
     */
    @TableField("is_link")
    private String isLink;

    /**
     * 是否隐藏菜单项（0=显示，1=隐藏）
     */
    @TableField("is_hide")
    private Boolean isHide;

    /**
     * 是否全屏显示（如登录页）
     */
    @TableField("is_full")
    private Boolean isFull;

    /**
     * 是否固定标签（常驻 tab）
     */
    @TableField("is_affix")
    private Boolean isAffix;

    /**
     * 是否缓存组件（keep-alive）
     */
    @TableField("is_keep_alive")
    private Boolean isKeepAlive;

    /**
     * 高亮菜单路径（用于详情页返回父级）
     */
    @TableField("active_menu")
    private String activeMenu;

    /**
     * 是否不继承父级 affix 属性（扩展字段）
     */
    @TableField("no_affix_parent")
    private Boolean noAffixParent;

    /**
     * 排序权重，值越小越靠前
     */
    @TableField("sort_order")
    private Integer sortOrder;

    /**
     * 逻辑删除：0=未删除，1=已删除
     */
    @TableLogic
    @TableField("is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

}
