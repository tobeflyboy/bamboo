create table sys_permission  (
    id TEXT PRIMARY KEY,                            -- 路由ID
    parent_id TEXT DEFAULT NULL,                    -- 父级路由ID，根节点为 NULL
    path TEXT NOT NULL,                             -- 路由路径，如 '/proTable/useProTable'
    name TEXT UNIQUE NOT NULL,                      -- 路由名称，唯一，用于编程式导航
    component TEXT,                                 -- 组件路径（懒加载），如 '/proTable/useProTable/index'
    redirect TEXT,                                  -- 重定向路径
    icon TEXT,                                      -- 菜单图标（可选默认值）
    title TEXT NOT NULL,                            -- 菜单/页面标题
    is_link TEXT DEFAULT '',                        -- 外部链接地址，为空表示内部路由
    is_hide BOOLEAN DEFAULT 0,                      -- 是否隐藏菜单项（0=显示，1=隐藏）
    is_full BOOLEAN DEFAULT 0,                      -- 是否全屏显示（如登录页）
    is_affix BOOLEAN DEFAULT 0,                     -- 是否固定标签（常驻 tab）
    is_keep_alive BOOLEAN DEFAULT 1,                -- 是否缓存组件（keep-alive）
    active_menu TEXT DEFAULT NULL,                  -- 高亮菜单路径（用于详情页返回父级）
    no_affix_parent BOOLEAN DEFAULT 0,              -- 是否不继承父级 affix 属性（扩展字段）
    sort_order INTEGER DEFAULT 0,                   -- 排序权重，值越小越靠前
    is_deleted BOOLEAN DEFAULT 0,                   -- 逻辑删除
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP, -- 创建时间
    update_time DATETIME                            -- 更新时间
);

create table sys_role  (
    id TEXT not null,
    role_code TEXT not null,
    role_name TEXT not null,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_by TEXT,
    primary key (id)
);

create table sys_role_permission  (
    id TEXT not null,
    role_id TEXT not null,
    permission_id TEXT not null,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_by TEXT,
    primary key (id)
);

create table sys_user  (
    id TEXT not null,
    username TEXT not null,
    password TEXT not null,
    salt TEXT not null,
    real_name TEXT,
    email TEXT,
    mobile TEXT,
    openid TEXT,
    status integer not null,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_by TEXT,
    update_time DATETIME,
    update_by TEXT,
    last_login_time DATETIME,
    primary key (id)
);

create table sys_user_role  (
    id TEXT not null,
    user_id TEXT not null,
    role_id TEXT not null,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_by TEXT
);

create table sys_config (
    id TEXT not null,
    config_name TEXT not null,
    config_key TEXT not null,
    config_value TEXT not null,
    remark TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    create_by TEXT,
    update_time DATETIME,
    update_by TEXT,
    is_deleted integer default 0 not null,
    primary key (id)
);
-- 1. 根节点（层级1）
INSERT INTO sys_permission(id, parent_id, path, name, component, redirect, icon, title, is_link, is_hide, is_full, is_affix, is_keep_alive, active_menu, no_affix_parent, sort_order, is_deleted, create_time, update_time) VALUES
('1000000000000000001', NULL, '/home/index', 'home', '/home/index', NULL, 'HomeFilled', '首页', '', 0, 0, 1, 1, NULL, 0, 1, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000002', NULL, '/auth', 'auth', NULL, '/auth/permission', 'Lock', '权限管理', '', 0, 0, 0, 1, NULL, 0, 2, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000003', NULL, '/gushici', 'gushici', NULL, '/gushici/tengwanggexu', 'Notebook', '古诗词', '', 0, 0, 0, 1, NULL, 0, 3, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000004', NULL, '/dataScreen', 'dataScreen', '/dataScreen/index', NULL, 'Histogram', '数据大屏', '', 0, 1, 0, 1, NULL, 0, 4, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000005', NULL, '/proTable', 'proTable', NULL, '/proTable/useProTable', 'MessageBox', '超级表格', '', 0, 0, 0, 1, NULL, 0, 5, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000006', NULL, '/auth2', 'auth2', NULL, '/auth/menu', 'Lock', '权限管理2', '', 0, 0, 0, 1, NULL, 0, 6, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000007', NULL, '/assembly', 'assembly', NULL, '/assembly/guide', 'Briefcase', '常用组件', '', 0, 0, 0, 1, NULL, 0, 7, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000008', NULL, '/dashboard', 'dashboard', NULL, '/dashboard/dataVisualize', 'Odometer', 'Dashboard', '', 0, 0, 0, 1, NULL, 0, 8, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000009', NULL, '/form', 'form', NULL, '/form/proForm', 'Tickets', '表单 Form', '', 0, 0, 0, 1, NULL, 0, 9, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000010', NULL, '/echarts', 'echarts', NULL, '/echarts/waterChart', 'TrendCharts', 'ECharts', '', 0, 0, 0, 1, NULL, 0, 10, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000011', NULL, '/directives', 'directives', NULL, '/directives/copyDirect', 'Stamp', '自定义指令', '', 0, 0, 0, 1, NULL, 0, 11, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000012', NULL, '/menu', 'menu', NULL, '/menu/menu1', 'List', '菜单嵌套', '', 0, 0, 0, 1, NULL, 0, 12, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000013', NULL, '/system', 'system', NULL, '/system/accountManage', 'Tools', '系统管理', '', 0, 0, 0, 1, NULL, 0, 13, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000014', NULL, '/link', 'link', NULL, '/link/bing', 'Paperclip', '外部链接', '', 0, 0, 0, 1, NULL, 0, 14, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000015', NULL, '/about/index', 'about', '/about/index', NULL, 'InfoFilled', '关于项目', '', 0, 0, 0, 1, NULL, 0, 15, 0, CURRENT_TIMESTAMP, NULL);

-- 2. 权限管理（层级2）
INSERT INTO sys_permission(id, parent_id, path, name, component, redirect, icon, title, is_link, is_hide, is_full, is_affix, is_keep_alive, active_menu, no_affix_parent, sort_order, is_deleted, create_time, update_time) VALUES
('1000000000000000101', '1000000000000000002', '/auth/permission', 'permission', '/authorities/permission/permission', NULL, 'Files', '资源管理', '', 0, 0, 0, 1, NULL, 0, 101, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000102', '1000000000000000002', '/auth/role', 'role', '/authorities/role/role', NULL, 'Finished', '角色管理', '', 0, 0, 0, 1, NULL, 0, 102, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000103', '1000000000000000002', '/auth/user', 'user', '/authorities/user/user', NULL, 'UserFilled', '用户管理', '', 0, 0, 0, 1, NULL, 0, 103, 0, CURRENT_TIMESTAMP, NULL);

-- 3. 古诗词二级（层级2）
INSERT INTO sys_permission(id, parent_id, path, name, component, redirect, icon, title, is_link, is_hide, is_full, is_affix, is_keep_alive, active_menu, no_affix_parent, sort_order, is_deleted, create_time, update_time) VALUES
('1000000000000000201', '1000000000000000003', '/gushici/tengwanggexu', 'tengwanggexu', '/gushici/滕王阁序', NULL, 'Tickets', '滕王阁序', '', 0, 0, 0, 1, NULL, 0, 201, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000202', '1000000000000000003', '/gushici/jiangjinjiu', 'jiangjinjiu', '/gushici/将进酒', NULL, 'Tickets', '将进酒', '', 0, 0, 0, 1, NULL, 0, 202, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000203', '1000000000000000003', '/gushici/manjianghong', 'manjianghong', '/gushici/满江红·写怀', NULL, 'Tickets', '满江红·写怀', '', 0, 0, 0, 1, NULL, 0, 203, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000204', '1000000000000000003', '/gushici/yueyanglouji', 'yueyanglouji', '/gushici/岳阳楼记', NULL, 'Tickets', '岳阳楼记', '', 0, 0, 0, 1, NULL, 0, 204, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000205', '1000000000000000003', '/gushici/chushibiao', 'chushibiao', '/gushici/出师表', NULL, 'Tickets', '出师表', '', 0, 0, 0, 1, NULL, 0, 205, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000206', '1000000000000000003', '/gushici/xiaoyaoyou', 'xiaoyaoyou', '/gushici/逍遥游', NULL, 'Tickets', '逍遥游', '', 0, 0, 0, 1, NULL, 0, 206, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000207', '1000000000000000003', '/gushici/xiaoyaoyoujiexuan', 'xiaoyaoyoujiexuan', '/gushici/逍遥游节选', NULL, 'Tickets', '逍遥游节选', '', 0, 0, 0, 1, NULL, 0, 207, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000208', '1000000000000000003', '/gushici/duangexing', 'duangexing', '/gushici/短歌行', NULL, 'Tickets', '短歌行', '', 0, 0, 0, 1, NULL, 0, 208, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000209', '1000000000000000003', '/gushici/songdongyangmashengxu', 'songdongyangmashengxu', '/gushici/送东阳马生序', NULL, 'Tickets', '送东阳马生序', '', 0, 0, 0, 1, NULL, 0, 209, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000210', '1000000000000000003', '/gushici/dingfengbo', 'dingfengbo', '/gushici/定风波·莫听穿林打叶声', NULL, 'Tickets', '定风波·莫听穿林打叶声', '', 0, 0, 0, 1, NULL, 0, 210, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000211', '1000000000000000003', '/gushici/jiangchengziyimaozhengyueershiriyejimeng', 'jiangchengziyimaozhengyueershiriyejimeng', '/gushici/江城子·乙卯正月二十日夜记梦', NULL, 'Tickets', '江城子·乙卯正月二十日夜记梦', '', 0, 0, 0, 1, NULL, 0, 211, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000212', '1000000000000000003', '/gushici/jiangchengzimizhouchulie', 'jiangchengzimizhouchulie', '/gushici/江城子·密州出猎', NULL, 'Tickets', '江城子·密州出猎', '', 0, 0, 0, 1, NULL, 0, 212, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000213', '1000000000000000003', '/gushici/taohuaange', 'taohuaange', '/gushici/桃花庵歌', NULL, 'Tickets', '桃花庵歌', '', 0, 0, 0, 1, NULL, 0, 213, 0, CURRENT_TIMESTAMP, NULL);

-- 4. 其余二级路由
INSERT INTO sys_permission(id, parent_id, path, name, component, redirect, icon, title, is_link, is_hide, is_full, is_affix, is_keep_alive, active_menu, no_affix_parent, sort_order, is_deleted, create_time, update_time) VALUES
('1000000000000000301', '1000000000000000005', '/proTable/useProTable', 'useProTable', '/proTable/useProTable/index', NULL, 'Menu', '使用 ProTable', '', 0, 0, 0, 1, NULL, 0, 301, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000302', '1000000000000000005', '/proTable/useTreeFilter', 'useTreeFilter', '/proTable/useTreeFilter/index', NULL, 'Menu', '使用 TreeFilter', '', 0, 0, 0, 1, NULL, 0, 302, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000303', '1000000000000000005', '/proTable/useSelectFilter', 'useSelectFilter', '/proTable/useSelectFilter/index', NULL, 'Menu', '使用 SelectFilter', '', 0, 0, 0, 1, NULL, 0, 303, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000304', '1000000000000000005', '/proTable/treeProTable', 'treeProTable', '/proTable/treeProTable/index', NULL, 'Menu', '树形 ProTable', '', 0, 0, 0, 1, NULL, 0, 304, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000305', '1000000000000000005', '/proTable/complexProTable', 'complexProTable', '/proTable/complexProTable/index', NULL, 'Menu', '复杂 ProTable', '', 0, 0, 0, 1, NULL, 0, 305, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000306', '1000000000000000005', '/proTable/document', 'proTableDocument', '/proTable/document/index', 'https://juejin.cn/post/7166068828202336263/#heading-14', 'Menu', 'ProTable 文档', '', 0, 0, 0, 1, NULL, 0, 306, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000401', '1000000000000000006', '/auth/menu', 'authMenu', '/auth/menu/index', NULL, 'Menu', '菜单权限', '', 0, 0, 0, 1, NULL, 0, 401, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000402', '1000000000000000006', '/auth/button', 'authButton', '/auth/button/index', NULL, 'Menu', '按钮权限', '', 0, 0, 0, 1, NULL, 0, 402, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000501', '1000000000000000007', '/assembly/guide', 'guide', '/assembly/guide/index', NULL, 'Menu', '引导页', '', 0, 0, 0, 1, NULL, 0, 501, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000502', '1000000000000000007', '/assembly/tabs', 'tabs', '/assembly/tabs/index', NULL, 'Menu', '标签页操作', '', 0, 0, 0, 1, NULL, 0, 502, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000503', '1000000000000000007', '/assembly/selectIcon', 'selectIcon', '/assembly/selectIcon/index', NULL, 'Menu', '图标选择器', '', 0, 0, 0, 1, NULL, 0, 503, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000504', '1000000000000000007', '/assembly/selectFilter', 'selectFilter', '/assembly/selectFilter/index', NULL, 'Menu', '分类筛选器', '', 0, 0, 0, 1, NULL, 0, 504, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000505', '1000000000000000007', '/assembly/treeFilter', 'treeFilter', '/assembly/treeFilter/index', NULL, 'Menu', '树形筛选器', '', 0, 0, 0, 1, NULL, 0, 505, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000506', '1000000000000000007', '/assembly/svgIcon', 'svgIcon', '/assembly/svgIcon/index', NULL, 'Menu', 'SVG 图标', '', 0, 0, 0, 1, NULL, 0, 506, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000507', '1000000000000000007', '/assembly/uploadFile', 'uploadFile', '/assembly/uploadFile/index', NULL, 'Menu', '文件上传', '', 0, 0, 0, 1, NULL, 0, 507, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000508', '1000000000000000007', '/assembly/batchImport', 'batchImport', '/assembly/batchImport/index', NULL, 'Menu', '批量添加数据', '', 0, 0, 0, 1, NULL, 0, 508, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000509', '1000000000000000007', '/assembly/wangEditor', 'wangEditor', '/assembly/wangEditor/index', NULL, 'Menu', '富文本编辑器', '', 0, 0, 0, 1, NULL, 0, 509, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000510', '1000000000000000007', '/assembly/draggable', 'draggable', '/assembly/draggable/index', NULL, 'Menu', '拖拽组件', '', 0, 0, 0, 1, NULL, 0, 510, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000601', '1000000000000000008', '/dashboard/dataVisualize', 'dataVisualize', '/dashboard/dataVisualize/index', NULL, 'Menu', '数据可视化', '', 0, 0, 0, 1, NULL, 0, 601, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000701', '1000000000000000009', '/form/proForm', 'proForm', '/form/proForm/index', NULL, 'Menu', '超级 Form', '', 0, 0, 0, 1, NULL, 0, 701, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000702', '1000000000000000009', '/form/basicForm', 'basicForm', '/form/basicForm/index', NULL, 'Menu', '基础 Form', '', 0, 0, 0, 1, NULL, 0, 702, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000703', '1000000000000000009', '/form/validateForm', 'validateForm', '/form/validateForm/index', NULL, 'Menu', '校验 Form', '', 0, 0, 0, 1, NULL, 0, 703, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000704', '1000000000000000009', '/form/dynamicForm', 'dynamicForm', '/form/dynamicForm/index', NULL, 'Menu', '动态 Form', '', 0, 0, 0, 1, NULL, 0, 704, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000801', '1000000000000000010', '/echarts/waterChart', 'waterChart', '/echarts/waterChart/index', NULL, 'Menu', '水型图', '', 0, 0, 0, 1, NULL, 0, 801, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000802', '1000000000000000010', '/echarts/columnChart', 'columnChart', '/echarts/columnChart/index', NULL, 'Menu', '柱状图', '', 0, 0, 0, 1, NULL, 0, 802, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000803', '1000000000000000010', '/echarts/lineChart', 'lineChart', '/echarts/lineChart/index', NULL, 'Menu', '折线图', '', 0, 0, 0, 1, NULL, 0, 803, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000804', '1000000000000000010', '/echarts/pieChart', 'pieChart', '/echarts/pieChart/index', NULL, 'Menu', '饼图', '', 0, 0, 0, 1, NULL, 0, 804, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000805', '1000000000000000010', '/echarts/radarChart', 'radarChart', '/echarts/radarChart/index', NULL, 'Menu', '雷达图', '', 0, 0, 0, 1, NULL, 0, 805, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000806', '1000000000000000010', '/echarts/nestedChart', 'nestedChart', '/echarts/nestedChart/index', NULL, 'Menu', '嵌套环形图', '', 0, 0, 0, 1, NULL, 0, 806, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000901', '1000000000000000011', '/directives/copyDirect', 'copyDirect', '/directives/copyDirect/index', NULL, 'Menu', '复制指令', '', 0, 0, 0, 1, NULL, 0, 901, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000902', '1000000000000000011', '/directives/watermarkDirect', 'watermarkDirect', '/directives/watermarkDirect/index', NULL, 'Menu', '水印指令', '', 0, 0, 0, 1, NULL, 0, 902, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000903', '1000000000000000011', '/directives/dragDirect', 'dragDirect', '/directives/dragDirect/index', NULL, 'Menu', '拖拽指令', '', 0, 0, 0, 1, NULL, 0, 903, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000904', '1000000000000000011', '/directives/debounceDirect', 'debounceDirect', '/directives/debounceDirect/index', NULL, 'Menu', '防抖指令', '', 0, 0, 0, 1, NULL, 0, 904, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000905', '1000000000000000011', '/directives/throttleDirect', 'throttleDirect', '/directives/throttleDirect/index', NULL, 'Menu', '节流指令', '', 0, 0, 0, 1, NULL, 0, 905, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000000906', '1000000000000000011', '/directives/longpressDirect', 'longpressDirect', '/directives/longpressDirect/index', NULL, 'Menu', '长按指令', '', 0, 0, 0, 1, NULL, 0, 906, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001001', '1000000000000000012', '/menu/menu1', 'menu1', '/menu/menu1/index', NULL, 'Menu', '菜单1', '', 0, 0, 0, 1, NULL, 0, 1001, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001002', '1000000000000000012', '/menu/menu2', 'menu2', NULL, '/menu/menu2/menu21', 'Menu', '菜单2', '', 0, 0, 0, 1, NULL, 0, 1002, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001003', '1000000000000000012', '/menu/menu3', 'menu3', '/menu/menu3/index', NULL, 'Menu', '菜单3', '', 0, 0, 0, 1, NULL, 0, 1003, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001101', '1000000000000000013', '/system/accountManage', 'accountManage', '/system/accountManage/index', NULL, 'Menu', '账号管理', '', 0, 0, 0, 1, NULL, 0, 1101, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001102', '1000000000000000013', '/system/roleManage', 'roleManage', '/system/roleManage/index', NULL, 'Menu', '角色管理', '', 0, 0, 0, 1, NULL, 0, 1102, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001103', '1000000000000000013', '/system/menuManage', 'menuManage', '/system/menuManage/index', NULL, 'Menu', '菜单管理', '', 0, 0, 0, 1, NULL, 0, 1103, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001104', '1000000000000000013', '/system/departmentManage', 'departmentManage', '/system/departmentManage/index', NULL, 'Menu', '部门管理', '', 0, 0, 0, 1, NULL, 0, 1104, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001105', '1000000000000000013', '/system/dictManage', 'dictManage', '/system/dictManage/index', NULL, 'Menu', '字典管理', '', 0, 0, 0, 1, NULL, 0, 1105, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001106', '1000000000000000013', '/system/timingTask', 'timingTask', '/system/timingTask/index', NULL, 'Menu', '定时任务', '', 0, 0, 0, 1, NULL, 0, 1106, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001107', '1000000000000000013', '/system/systemLog', 'systemLog', '/system/systemLog/index', NULL, 'Menu', '系统日志', '', 0, 0, 0, 1, NULL, 0, 1107, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001201', '1000000000000000014', '/link/bing', 'bing', '/link/bing/index', NULL, 'Menu', 'Bing 内嵌', '', 0, 0, 0, 1, NULL, 0, 1201, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001202', '1000000000000000014', '/link/gitee', 'gitee', '/link/gitee/index', 'https://gitee.com/haimashale/geeker-admin-js', 'Menu', 'Gitee 仓库', '', 0, 0, 0, 1, NULL, 0, 1202, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001203', '1000000000000000014', '/link/github', 'github', '/link/github/index', 'https://github.com/lw123lw/Geeker-Admin-JS', 'Menu', 'GitHub 仓库', '', 0, 0, 0, 1, NULL, 0, 1203, 0, CURRENT_TIMESTAMP, NULL),
('1000000000000001204', '1000000000000000014', '/link/docs', 'docs', '/link/docs/index', 'https://docs.spicyboy.cn', 'Menu', '项目文档', '', 0, 0, 0, 1, NULL, 0, 1204, 0, CURRENT_TIMESTAMP, NULL);

-- 5. 三级路由（层级3）
INSERT INTO sys_permission(id, parent_id, path, name, component, redirect, icon, title, is_link, is_hide, is_full, is_affix, is_keep_alive, active_menu, no_affix_parent, sort_order, is_deleted, create_time, update_time) VALUES
('1000000000000001205', '1000000000000000014', '/link/juejin', 'juejin', '/link/juejin/index', 'https://juejin.cn/user/3263814531551816/posts', 'Menu', '掘金主页', '', 0, 0, 0, 1, NULL, 0, 1205, 0, CURRENT_TIMESTAMP, NULL),
('10000000000000003011', '1000000000000000301', '/proTable/useProTable/detail/:id', 'useProTableDetail', '/proTable/useProTable/detail', NULL, 'Menu', 'ProTable 详情', '', 1, 0, 0, 1, '/proTable/useProTable', 0, 3011, 0, CURRENT_TIMESTAMP, NULL),
('10000000000000003021', '1000000000000000302', '/proTable/useTreeFilter/detail/:id', 'useTreeFilterDetail', '/proTable/useTreeFilter/detail', NULL, 'Menu', 'TreeFilter 详情', '', 1, 0, 0, 1, '/proTable/useTreeFilter', 0, 3021, 0, CURRENT_TIMESTAMP, NULL),
('10000000000000005011', '1000000000000000502', '/assembly/tabs/detail/:id', 'tabsDetail', '/assembly/tabs/detail', NULL, 'Menu', 'Tab 详情', '', 1, 0, 0, 1, '/assembly/tabs', 0, 5021, 0, CURRENT_TIMESTAMP, NULL),
('10000000000000010021', '1000000000000001002', '/menu/menu2/menu21', 'menu21', '/menu/menu2/menu21/index', NULL, 'Menu', '菜单2-1', '', 0, 0, 0, 1, NULL, 0, 10021, 0, CURRENT_TIMESTAMP, NULL),
('10000000000000010022', '1000000000000001002', '/menu/menu2/menu22', 'menu22', NULL, '/menu/menu2/menu22/menu221', 'Menu', '菜单2-2', '', 0, 0, 0, 1, NULL, 0, 10022, 0, CURRENT_TIMESTAMP, NULL),
('10000000000000010023', '1000000000000001002', '/menu/menu2/menu23', 'menu23', '/menu/menu2/menu23/index', NULL, 'Menu', '菜单2-3', '', 0, 0, 0, 1, NULL, 0, 10023, 0, CURRENT_TIMESTAMP, NULL);

-- 6. 四级路由（层级4）
INSERT INTO sys_permission(id, parent_id, path, name, component, redirect, icon, title, is_link, is_hide, is_full, is_affix, is_keep_alive, active_menu, no_affix_parent, sort_order, is_deleted, create_time, update_time) VALUES
('100000000000000100221', '10000000000000010022', '/menu/menu2/menu22/menu221', 'menu221', '/menu/menu2/menu22/menu221/index', NULL, 'Menu', '菜单2-2-1', '', 0, 0, 0, 1, NULL, 0, 100221, 0, CURRENT_TIMESTAMP, NULL),
('100000000000000100222', '10000000000000010022', '/menu/menu2/menu22/menu222', 'menu222', '/menu/menu2/menu22/menu222/index', NULL, 'Menu', '菜单2-2-2', '', 0, 0, 0, 1, NULL, 0, 100222, 0, CURRENT_TIMESTAMP, NULL);


-- 初始化角色
insert into sys_role(id, role_code, role_name, create_by) values
('9999999999999999999', 'ROLE_ADMIN', '管理员', null);

-- 初始化角色菜单权限
insert into sys_role_permission(id, role_id, permission_id)
select
 1000000000000000000 + (row_number() over(order by p.id)) as id,
 r.id as role_id,
 p.id as permission_id
from sys_permission p, sys_role r;

-- 初始化用户
INSERT INTO sys_user (id, username, password, salt, real_name, email, status, create_time) VALUES 
('8888888888888888888', 'admin', '1bb7a038b8316d0d7663020ad3e76840b847ce08eb6a18000212afc91f0701ff', 'kavzSvUupJ', '管理员', 'admin@nutcracker.com', 1, CURRENT_TIMESTAMP);

-- 用户角色关系表
INSERT INTO sys_user_role (id, user_id, role_id, create_time, create_by) VALUES ('1000000000000000001', '8888888888888888888', '9999999999999999999', CURRENT_TIMESTAMP, NULL);

-- 系统配置表
insert into sys_config(id, config_name, config_key, config_value, remark) values ('1000000000000000001', '系统限流QPS', 'IP_QPS_THRESHOLD_LIMIT', '10', '单个IP请求的最大每秒查询数（QPS）阈值Key');


select * from sys_permission;
select * from sys_role;
select * from sys_role_permission;
select * from sys_user;
select * from sys_user_role;
select * from sys_config;

/*
drop table sys_permission;
drop table sys_role;
drop table sys_role_permission;
drop table sys_user;
drop table sys_user_role;
drop table sys_config;

delete from sys_permission;
delete from sys_role;
delete from sys_role_permission;
delete from sys_user;
delete from sys_user_role;
delete from sys_config;
*/

