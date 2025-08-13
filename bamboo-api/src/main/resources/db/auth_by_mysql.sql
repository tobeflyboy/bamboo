-- 权限表
CREATE TABLE sys_permission (
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    permission_code VARCHAR(100) NOT NULL COMMENT '权限编码',
    permission_name VARCHAR(200) NOT NULL COMMENT '权限名称',
    parent_permission_code VARCHAR(100) COMMENT '父级权限编码',
    url VARCHAR(500) COMMENT '菜单URL',
    icon VARCHAR(50) COMMENT '菜单图标',
    hide INTEGER COMMENT '是否隐藏(0:显示 1:隐藏)',
    lev INTEGER COMMENT '菜单层级',
    sort INTEGER COMMENT '排序号',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    create_by VARCHAR(32) COMMENT '创建人',
    PRIMARY KEY (id)
) COMMENT = '系统权限表';

-- 角色表
CREATE TABLE sys_role (
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    role_code VARCHAR(100) NOT NULL COMMENT '角色编码',
    role_name VARCHAR(100) NOT NULL COMMENT '角色名称',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    create_by VARCHAR(32) COMMENT '创建人',
    PRIMARY KEY (id)
) COMMENT = '系统角色表';

-- 角色权限关联表
CREATE TABLE sys_role_permission (
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    role_id VARCHAR(32) NOT NULL COMMENT '角色ID',
    permission_id VARCHAR(32) NOT NULL COMMENT '权限ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    create_by VARCHAR(32) COMMENT '创建人',
    PRIMARY KEY (id)
) COMMENT = '角色权限关联表';

-- 用户表
CREATE TABLE sys_user (
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    salt VARCHAR(50) NOT NULL COMMENT '密码盐',
    real_name VARCHAR(50) COMMENT '真实姓名',
    email VARCHAR(100) COMMENT '邮箱',
    mobile VARCHAR(100) COMMENT '手机',
    openid VARCHAR(100) COMMENT '微信openid',
    status INTEGER NOT NULL COMMENT '状态(0:禁用 1:启用)',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    create_by VARCHAR(32) COMMENT '创建人',
    update_time DATETIME COMMENT '更新时间',
    update_by VARCHAR(32) COMMENT '更新人',
    last_login_time VARCHAR(50) COMMENT '最后登录时间',
    PRIMARY KEY (id)
) COMMENT = '系统用户表';

-- 用户角色关联表
CREATE TABLE sys_user_role (
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    user_id VARCHAR(32) NOT NULL COMMENT '用户ID',
    role_id VARCHAR(32) NOT NULL COMMENT '角色ID',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    create_by VARCHAR(32) COMMENT '创建人'
) COMMENT = '用户角色关联表';

-- 系统配置表
CREATE TABLE sys_config (
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    config_name VARCHAR(50) NOT NULL COMMENT '配置名称',
    config_key VARCHAR(50) NOT NULL COMMENT '配置键',
    config_value VARCHAR(100) NOT NULL COMMENT '配置值',
    remark VARCHAR(255) COMMENT '备注',
    create_time DATETIME COMMENT '创建时间',
    create_by VARCHAR(32) COMMENT '创建人',
    update_time DATETIME COMMENT '更新时间',
    update_by VARCHAR(32) COMMENT '更新人',
    is_deleted INTEGER DEFAULT 0 NOT NULL COMMENT '删除标记(0:正常 1:删除)',
    PRIMARY KEY (id)
) COMMENT = '系统配置表';

-- 系统日志表（保留注释的创建语句）
CREATE TABLE sys_log  (
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    username VARCHAR(100) NULL DEFAULT NULL COMMENT '用户名',
    ip_address VARCHAR(100) NOT NULL COMMENT 'IP地址',
    ip_source VARCHAR(100) NOT NULL COMMENT 'IP来源',
    message VARCHAR(100) NULL DEFAULT NULL COMMENT '操作信息',
    browser_name VARCHAR(100) NULL DEFAULT NULL COMMENT '浏览器名称',
    system_name VARCHAR(100) NOT NULL COMMENT '操作系统',
    create_time DATETIME NOT NULL COMMENT '创建时间',
    PRIMARY KEY (id)
) COMMENT = '系统日志表';

-- 配置数据示例
insert into sys_config values (1, '系统限流QPS', 'IP_QPS_THRESHOLD_LIMIT', '10', '单个IP请求的最大每秒查询数（QPS）阈值Key', now(), 1, NULL, NULL, 0);


-- 查询语句（保持不变）
SELECT * FROM sys_permission;
SELECT * FROM sys_role;
SELECT * FROM sys_role_permission;
SELECT * FROM sys_user;
SELECT * FROM sys_user_role;

/* 
-- 表删除语句（保持不变）
DROP TABLE sys_permission;
DROP TABLE sys_role;
DROP TABLE sys_role_permission;
DROP TABLE sys_user;
DROP TABLE sys_user_role;
*/