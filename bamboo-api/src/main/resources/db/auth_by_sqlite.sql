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
('1000000000000000000', NULL, '/home/index', 'home', '/home/index', NULL, 'HomeFilled', '首页', '', 0, 0, 1, 1, NULL, 0,  0, 0, CURRENT_TIMESTAMP, NULL),
('1001000000000000000', NULL, '/gushici', 'gushici', NULL, '/gushici/tengwanggexu', 'Notebook', '古诗词', '', 0, 0, 0, 1, NULL, 0, 10, 0, CURRENT_TIMESTAMP, NULL),
('1002000000000000000', NULL, '/dataScreen', 'dataScreen', '/dataScreen/index', NULL, 'Histogram', '数据大屏', '', 0, 1, 0, 1, NULL, 0, 20, 0, CURRENT_TIMESTAMP, NULL),
('1003000000000000000', NULL, '/proTable', 'proTable', NULL, '/proTable/useProTable', 'MessageBox', '超级表格', '', 0, 0, 0, 1, NULL, 0, 30, 0, CURRENT_TIMESTAMP, NULL),
('1004000000000000000', NULL, '/auth', 'auth', NULL, '/auth/menu', 'Lock', '权限管理', '', 0, 0, 0, 1, NULL, 0, 40, 0, CURRENT_TIMESTAMP, NULL),
('1005000000000000000', NULL, '/assembly', 'assembly', NULL, '/assembly/guide', 'Briefcase', '常用组件', '', 0, 0, 0, 1, NULL, 0, 50, 0, CURRENT_TIMESTAMP, NULL),
('1006000000000000000', NULL, '/dashboard', 'dashboard', NULL, '/dashboard/dataVisualize', 'Odometer', 'Dashboard', '', 0, 0, 0, 1, NULL, 0, 60, 0, CURRENT_TIMESTAMP, NULL),
('1007000000000000000', NULL, '/form', 'form', NULL, '/form/proForm', 'Tickets', '表单 Form', '', 0, 0, 0, 1, NULL, 0, 70, 0, CURRENT_TIMESTAMP, NULL),
('1008000000000000000', NULL, '/echarts', 'echarts', NULL, '/echarts/waterChart', 'TrendCharts', 'ECharts', '', 0, 0, 0, 1, NULL, 0, 80, 0, CURRENT_TIMESTAMP, NULL),
('1009000000000000000', NULL, '/directives', 'directives', NULL, '/directives/copyDirect', 'Stamp', '自定义指令', '', 0, 0, 0, 1, NULL, 0, 90, 0, CURRENT_TIMESTAMP, NULL),
('1010000000000000000', NULL, '/menu', 'menu', NULL, '/menu/menu1', 'List', '菜单嵌套', '', 0, 0, 0, 1, NULL, 0,100, 0, CURRENT_TIMESTAMP, NULL),
('1011000000000000000', NULL, '/system', 'system', NULL, '/system/accountManage', 'Tools', '系统管理', '', 0, 0, 0, 1, NULL, 0,110, 0, CURRENT_TIMESTAMP, NULL),
('1012000000000000000', NULL, '/link', 'link', NULL, '/link/bing', 'Paperclip', '外部链接', '', 0, 0, 0, 1, NULL, 0,120, 0, CURRENT_TIMESTAMP, NULL),
('1013000000000000000', NULL, '/about/index', 'about', '/about/index', NULL, 'InfoFilled', '关于项目', '', 0, 0, 0, 1, NULL, 0,130, 0, CURRENT_TIMESTAMP, NULL);

-- 2. 古诗词二级（层级2）
INSERT INTO sys_permission(id, parent_id, path, name, component, redirect, icon, title, is_link, is_hide, is_full, is_affix, is_keep_alive, active_menu, no_affix_parent, sort_order, is_deleted, create_time, update_time) VALUES
('1001010000000000000', '1001000000000000000', '/gushici/tengwanggexu', 'tengwanggexu', '/gushici/滕王阁序', NULL, 'Tickets', '滕王阁序', '', 0, 0, 0, 1, NULL, 0, 11, 0, CURRENT_TIMESTAMP, NULL),
('1001020000000000000', '1001000000000000000', '/gushici/jiangjinjiu', 'jiangjinjiu', '/gushici/将进酒', NULL, 'Tickets', '将进酒', '', 0, 0, 0, 1, NULL, 0, 12, 0, CURRENT_TIMESTAMP, NULL),
('1001030000000000000', '1001000000000000000', '/gushici/manjianghong', 'manjianghong', '/gushici/满江红·写怀', NULL, 'Tickets', '满江红·写怀', '', 0, 0, 0, 1, NULL, 0, 13, 0, CURRENT_TIMESTAMP, NULL),
('1001040000000000000', '1001000000000000000', '/gushici/yueyanglouji', 'yueyanglouji', '/gushici/岳阳楼记', NULL, 'Tickets', '岳阳楼记', '', 0, 0, 0, 1, NULL, 0, 14, 0, CURRENT_TIMESTAMP, NULL),
('1001050000000000000', '1001000000000000000', '/gushici/chushibiao', 'chushibiao', '/gushici/出师表', NULL, 'Tickets', '出师表', '', 0, 0, 0, 1, NULL, 0, 15, 0, CURRENT_TIMESTAMP, NULL),
('1001060000000000000', '1001000000000000000', '/gushici/xiaoyaoyou', 'xiaoyaoyou', '/gushici/逍遥游', NULL, 'Tickets', '逍遥游', '', 0, 0, 0, 1, NULL, 0, 16, 0, CURRENT_TIMESTAMP, NULL),
('1001070000000000000', '1001000000000000000', '/gushici/xiaoyaoyoujiexuan', 'xiaoyaoyoujiexuan', '/gushici/逍遥游节选', NULL, 'Tickets', '逍遥游节选', '', 0, 0, 0, 1, NULL, 0, 17, 0, CURRENT_TIMESTAMP, NULL),
('1001080000000000000', '1001000000000000000', '/gushici/duangexing', 'duangexing', '/gushici/短歌行', NULL, 'Tickets', '短歌行', '', 0, 0, 0, 1, NULL, 0, 18, 0, CURRENT_TIMESTAMP, NULL),
('1001090000000000000', '1001000000000000000', '/gushici/songdongyangmashengxu', 'songdongyangmashengxu', '/gushici/送东阳马生序', NULL, 'Tickets', '送东阳马生序', '', 0, 0, 0, 1, NULL, 0, 19, 0, CURRENT_TIMESTAMP, NULL),
('1001100000000000000', '1001000000000000000', '/gushici/dingfengbo', 'dingfengbo', '/gushici/定风波·莫听穿林打叶声', NULL, 'Tickets', '定风波·莫听穿林打叶声', '', 0, 0, 0, 1, NULL, 0, 20, 0, CURRENT_TIMESTAMP, NULL),
('1001110000000000000', '1001000000000000000', '/gushici/jiangchengziyimaozhengyueershiriyejimeng', 'jiangchengziyimaozhengyueershiriyejimeng', '/gushici/江城子·乙卯正月二十日夜记梦', NULL, 'Tickets', '江城子·乙卯正月二十日夜记梦', '', 0, 0, 0, 1, NULL, 0, 21, 0, CURRENT_TIMESTAMP, NULL),
('1001120000000000000', '1001000000000000000', '/gushici/jiangchengzimizhouchulie', 'jiangchengzimizhouchulie', '/gushici/江城子·密州出猎', NULL, 'Tickets', '江城子·密州出猎', '', 0, 0, 0, 1, NULL, 0, 22, 0, CURRENT_TIMESTAMP, NULL),
('1001130000000000000', '1001000000000000000', '/gushici/taohuaange', 'taohuaange', '/gushici/桃花庵歌', NULL, 'Tickets', '桃花庵歌', '', 0, 0, 0, 1, NULL, 0, 23, 0, CURRENT_TIMESTAMP, NULL);

-- 3. 其余二级路由
INSERT INTO sys_permission(id, parent_id, path, name, component, redirect, icon, title, is_link, is_hide, is_full, is_affix, is_keep_alive, active_menu, no_affix_parent, sort_order, is_deleted, create_time, update_time) VALUES
('1003010000000000000', '1003000000000000000', '/proTable/useProTable', 'useProTable', '/proTable/useProTable/index', NULL, 'Menu', '使用 ProTable', '', 0, 0, 0, 1, NULL, 0, 31, 0, CURRENT_TIMESTAMP, NULL),
('1003020000000000000', '1003000000000000000', '/proTable/useTreeFilter', 'useTreeFilter', '/proTable/useTreeFilter/index', NULL, 'Menu', '使用 TreeFilter', '', 0, 0, 0, 1, NULL, 0, 32, 0, CURRENT_TIMESTAMP, NULL),
('1003030000000000000', '1003000000000000000', '/proTable/useSelectFilter', 'useSelectFilter', '/proTable/useSelectFilter/index', NULL, 'Menu', '使用 SelectFilter', '', 0, 0, 0, 1, NULL, 0, 33, 0, CURRENT_TIMESTAMP, NULL),
('1003040000000000000', '1003000000000000000', '/proTable/treeProTable', 'treeProTable', '/proTable/treeProTable/index', NULL, 'Menu', '树形 ProTable', '', 0, 0, 0, 1, NULL, 0, 34, 0, CURRENT_TIMESTAMP, NULL),
('1003050000000000000', '1003000000000000000', '/proTable/complexProTable', 'complexProTable', '/proTable/complexProTable/index', NULL, 'Menu', '复杂 ProTable', '', 0, 0, 0, 1, NULL, 0, 35, 0, CURRENT_TIMESTAMP, NULL),
('1003060000000000000', '1003000000000000000', '/proTable/document', 'proTableDocument', '/proTable/document/index', 'https://juejin.cn/post/7166068828202336263/#heading-14', 'Menu', 'ProTable 文档', '', 0, 0, 0, 1, NULL, 0, 36, 0, CURRENT_TIMESTAMP, NULL),
('1004010000000000000', '1004000000000000000', '/auth/menu', 'authMenu', '/auth/menu/index', NULL, 'Menu', '菜单权限', '', 0, 0, 0, 1, NULL, 0, 41, 0, CURRENT_TIMESTAMP, NULL),
('1004020000000000000', '1004000000000000000', '/auth/button', 'authButton', '/auth/button/index', NULL, 'Menu', '按钮权限', '', 0, 0, 0, 1, NULL, 0, 42, 0, CURRENT_TIMESTAMP, NULL),
('1005010000000000000', '1005000000000000000', '/assembly/guide', 'guide', '/assembly/guide/index', NULL, 'Menu', '引导页', '', 0, 0, 0, 1, NULL, 0, 51, 0, CURRENT_TIMESTAMP, NULL),
('1005020000000000000', '1005000000000000000', '/assembly/tabs', 'tabs', '/assembly/tabs/index', NULL, 'Menu', '标签页操作', '', 0, 0, 0, 1, NULL, 0, 52, 0, CURRENT_TIMESTAMP, NULL),
('1005030000000000000', '1005000000000000000', '/assembly/selectIcon', 'selectIcon', '/assembly/selectIcon/index', NULL, 'Menu', '图标选择器', '', 0, 0, 0, 1, NULL, 0, 53, 0, CURRENT_TIMESTAMP, NULL),
('1005040000000000000', '1005000000000000000', '/assembly/selectFilter', 'selectFilter', '/assembly/selectFilter/index', NULL, 'Menu', '分类筛选器', '', 0, 0, 0, 1, NULL, 0, 54, 0, CURRENT_TIMESTAMP, NULL),
('1005050000000000000', '1005000000000000000', '/assembly/treeFilter', 'treeFilter', '/assembly/treeFilter/index', NULL, 'Menu', '树形筛选器', '', 0, 0, 0, 1, NULL, 0, 55, 0, CURRENT_TIMESTAMP, NULL),
('1005060000000000000', '1005000000000000000', '/assembly/svgIcon', 'svgIcon', '/assembly/svgIcon/index', NULL, 'Menu', 'SVG 图标', '', 0, 0, 0, 1, NULL, 0, 56, 0, CURRENT_TIMESTAMP, NULL),
('1005070000000000000', '1005000000000000000', '/assembly/uploadFile', 'uploadFile', '/assembly/uploadFile/index', NULL, 'Menu', '文件上传', '', 0, 0, 0, 1, NULL, 0, 57, 0, CURRENT_TIMESTAMP, NULL),
('1005080000000000000', '1005000000000000000', '/assembly/batchImport', 'batchImport', '/assembly/batchImport/index', NULL, 'Menu', '批量添加数据', '', 0, 0, 0, 1, NULL, 0, 58, 0, CURRENT_TIMESTAMP, NULL),
('1005090000000000000', '1005000000000000000', '/assembly/wangEditor', 'wangEditor', '/assembly/wangEditor/index', NULL, 'Menu', '富文本编辑器', '', 0, 0, 0, 1, NULL, 0, 59, 0, CURRENT_TIMESTAMP, NULL),
('1005100000000000000', '1005000000000000000', '/assembly/draggable', 'draggable', '/assembly/draggable/index', NULL, 'Menu', '拖拽组件', '', 0, 0, 0, 1, NULL, 0, 60, 0, CURRENT_TIMESTAMP, NULL),
('1006010000000000000', '1006000000000000000', '/dashboard/dataVisualize', 'dataVisualize', '/dashboard/dataVisualize/index', NULL, 'Menu', '数据可视化', '', 0, 0, 0, 1, NULL, 0, 61, 0, CURRENT_TIMESTAMP, NULL),
('1007010000000000000', '1007000000000000000', '/form/proForm', 'proForm', '/form/proForm/index', NULL, 'Menu', '超级 Form', '', 0, 0, 0, 1, NULL, 0, 71, 0, CURRENT_TIMESTAMP, NULL),
('1007020000000000000', '1007000000000000000', '/form/basicForm', 'basicForm', '/form/basicForm/index', NULL, 'Menu', '基础 Form', '', 0, 0, 0, 1, NULL, 0, 72, 0, CURRENT_TIMESTAMP, NULL),
('1007030000000000000', '1007000000000000000', '/form/validateForm', 'validateForm', '/form/validateForm/index', NULL, 'Menu', '校验 Form', '', 0, 0, 0, 1, NULL, 0, 73, 0, CURRENT_TIMESTAMP, NULL),
('1007040000000000000', '1007000000000000000', '/form/dynamicForm', 'dynamicForm', '/form/dynamicForm/index', NULL, 'Menu', '动态 Form', '', 0, 0, 0, 1, NULL, 0, 74, 0, CURRENT_TIMESTAMP, NULL),
('1008010000000000000', '1008000000000000000', '/echarts/waterChart', 'waterChart', '/echarts/waterChart/index', NULL, 'Menu', '水型图', '', 0, 0, 0, 1, NULL, 0, 81, 0, CURRENT_TIMESTAMP, NULL),
('1008020000000000000', '1008000000000000000', '/echarts/columnChart', 'columnChart', '/echarts/columnChart/index', NULL, 'Menu', '柱状图', '', 0, 0, 0, 1, NULL, 0, 82, 0, CURRENT_TIMESTAMP, NULL),
('1008030000000000000', '1008000000000000000', '/echarts/lineChart', 'lineChart', '/echarts/lineChart/index', NULL, 'Menu', '折线图', '', 0, 0, 0, 1, NULL, 0, 83, 0, CURRENT_TIMESTAMP, NULL),
('1008040000000000000', '1008000000000000000', '/echarts/pieChart', 'pieChart', '/echarts/pieChart/index', NULL, 'Menu', '饼图', '', 0, 0, 0, 1, NULL, 0, 84, 0, CURRENT_TIMESTAMP, NULL),
('1008050000000000000', '1008000000000000000', '/echarts/radarChart', 'radarChart', '/echarts/radarChart/index', NULL, 'Menu', '雷达图', '', 0, 0, 0, 1, NULL, 0, 85, 0, CURRENT_TIMESTAMP, NULL),
('1008060000000000000', '1008000000000000000', '/echarts/nestedChart', 'nestedChart', '/echarts/nestedChart/index', NULL, 'Menu', '嵌套环形图', '', 0, 0, 0, 1, NULL, 0, 86, 0, CURRENT_TIMESTAMP, NULL),
('1009010000000000000', '1009000000000000000', '/directives/copyDirect', 'copyDirect', '/directives/copyDirect/index', NULL, 'Menu', '复制指令', '', 0, 0, 0, 1, NULL, 0, 91, 0, CURRENT_TIMESTAMP, NULL),
('1009020000000000000', '1009000000000000000', '/directives/watermarkDirect', 'watermarkDirect', '/directives/watermarkDirect/index', NULL, 'Menu', '水印指令', '', 0, 0, 0, 1, NULL, 0, 92, 0, CURRENT_TIMESTAMP, NULL),
('1009030000000000000', '1009000000000000000', '/directives/dragDirect', 'dragDirect', '/directives/dragDirect/index', NULL, 'Menu', '拖拽指令', '', 0, 0, 0, 1, NULL, 0, 93, 0, CURRENT_TIMESTAMP, NULL),
('1009040000000000000', '1009000000000000000', '/directives/debounceDirect', 'debounceDirect', '/directives/debounceDirect/index', NULL, 'Menu', '防抖指令', '', 0, 0, 0, 1, NULL, 0, 94, 0, CURRENT_TIMESTAMP, NULL),
('1009050000000000000', '1009000000000000000', '/directives/throttleDirect', 'throttleDirect', '/directives/throttleDirect/index', NULL, 'Menu', '节流指令', '', 0, 0, 0, 1, NULL, 0, 95, 0, CURRENT_TIMESTAMP, NULL),
('1009060000000000000', '1009000000000000000', '/directives/longpressDirect', 'longpressDirect', '/directives/longpressDirect/index', NULL, 'Menu', '长按指令', '', 0, 0, 0, 1, NULL, 0, 96, 0, CURRENT_TIMESTAMP, NULL),
('1010010000000000000', '1010000000000000000', '/menu/menu1', 'menu1', '/menu/menu1/index', NULL, 'Menu', '菜单1', '', 0, 0, 0, 1, NULL, 0,101, 0, CURRENT_TIMESTAMP, NULL),
('1010020000000000000', '1010000000000000000', '/menu/menu2', 'menu2', NULL, '/menu/menu2/menu21', 'Menu', '菜单2', '', 0, 0, 0, 1, NULL, 0,102, 0, CURRENT_TIMESTAMP, NULL),
('1010030000000000000', '1010000000000000000', '/menu/menu3', 'menu3', '/menu/menu3/index', NULL, 'Menu', '菜单3', '', 0, 0, 0, 1, NULL, 0,103, 0, CURRENT_TIMESTAMP, NULL),
('1011010000000000000', '1011000000000000000', '/system/accountManage', 'accountManage', '/system/accountManage/index', NULL, 'Menu', '账号管理', '', 0, 0, 0, 1, NULL, 0,111, 0, CURRENT_TIMESTAMP, NULL),
('1011020000000000000', '1011000000000000000', '/system/roleManage', 'roleManage', '/system/roleManage/index', NULL, 'Menu', '角色管理', '', 0, 0, 0, 1, NULL, 0,112, 0, CURRENT_TIMESTAMP, NULL),
('1011030000000000000', '1011000000000000000', '/system/menuMange', 'menuMange', '/system/menuMange/index', NULL, 'Menu', '菜单管理', '', 0, 0, 0, 1, NULL, 0,113, 0, CURRENT_TIMESTAMP, NULL),
('1011040000000000000', '1011000000000000000', '/system/departmentManage', 'departmentManage', '/system/departmentManage/index', NULL, 'Menu', '部门管理', '', 0, 0, 0, 1, NULL, 0,114, 0, CURRENT_TIMESTAMP, NULL),
('1011050000000000000', '1011000000000000000', '/system/dictManage', 'dictManage', '/system/dictManage/index', NULL, 'Menu', '字典管理', '', 0, 0, 0, 1, NULL, 0,115, 0, CURRENT_TIMESTAMP, NULL),
('1011060000000000000', '1011000000000000000', '/system/timingTask', 'timingTask', '/system/timingTask/index', NULL, 'Menu', '定时任务', '', 0, 0, 0, 1, NULL, 0,116, 0, CURRENT_TIMESTAMP, NULL),
('1011070000000000000', '1011000000000000000', '/system/systemLog', 'systemLog', '/system/systemLog/index', NULL, 'Menu', '系统日志', '', 0, 0, 0, 1, NULL, 0,117, 0, CURRENT_TIMESTAMP, NULL),
('1012010000000000000', '1012000000000000000', '/link/bing', 'bing', '/link/bing/index', NULL, 'Menu', 'Bing 内嵌', '', 0, 0, 0, 1, NULL, 0,121, 0, CURRENT_TIMESTAMP, NULL),
('1012020000000000000', '1012000000000000000', '/link/gitee', 'gitee', '/link/gitee/index', 'https://gitee.com/haimashale/geeker-admin-js', 'Menu', 'Gitee 仓库', '', 0, 0, 0, 1, NULL, 0,122, 0, CURRENT_TIMESTAMP, NULL),
('1012030000000000000', '1012000000000000000', '/link/github', 'github', '/link/github/index', 'https://github.com/lw123lw/Geeker-Admin-JS', 'Menu', 'GitHub 仓库', '', 0, 0, 0, 1, NULL, 0,123, 0, CURRENT_TIMESTAMP, NULL),
('1012040000000000000', '1012000000000000000', '/link/docs', 'docs', '/link/docs/index', 'https://docs.spicyboy.cn', 'Menu', '项目文档', '', 0, 0, 0, 1, NULL, 0,124, 0, CURRENT_TIMESTAMP, NULL);

-- 4. 三级路由（层级3）
INSERT INTO sys_permission(id, parent_id, path, name, component, redirect, icon, title, is_link, is_hide, is_full, is_affix, is_keep_alive, active_menu, no_affix_parent, sort_order, is_deleted, create_time, update_time) VALUES
('1012050000000000000', '1012000000000000000', '/link/juejin', 'juejin', '/link/juejin/index', 'https://juejin.cn/user/3263814531551816/posts', 'Menu', '掘金主页', '', 0, 0, 0, 1, NULL, 0,125, 0, CURRENT_TIMESTAMP, NULL),
('1003010100000000000', '1003010000000000000', '/proTable/useProTable/detail/:id', 'useProTableDetail', '/proTable/useProTable/detail', NULL, 'Menu', 'ProTable 详情', '', 1, 0, 0, 1, '/proTable/useProTable', 0, 311, 0, CURRENT_TIMESTAMP, NULL),
('1003020100000000000', '1003020000000000000', '/proTable/useTreeFilter/detail/:id', 'useTreeFilterDetail', '/proTable/useTreeFilter/detail', NULL, 'Menu', 'TreeFilter 详情', '', 1, 0, 0, 1, '/proTable/useTreeFilter', 0, 321, 0, CURRENT_TIMESTAMP, NULL),
('1005020100000000000', '1005020000000000000', '/assembly/tabs/detail/:id', 'tabsDetail', '/assembly/tabs/detail', NULL, 'Menu', 'Tab 详情', '', 1, 0, 0, 1, '/assembly/tabs', 0, 521, 0, CURRENT_TIMESTAMP, NULL),
('1010020100000000000', '1010020000000000000', '/menu/menu2/menu21', 'menu21', '/menu/menu2/menu21/index', NULL, 'Menu', '菜单2-1', '', 0, 0, 0, 1, NULL, 0,1021, 0, CURRENT_TIMESTAMP, NULL),
('1010020200000000000', '1010020000000000000', '/menu/menu2/menu22', 'menu22', NULL, '/menu/menu2/menu22/menu221', 'Menu', '菜单2-2', '', 0, 0, 0, 1, NULL, 0,1022, 0, CURRENT_TIMESTAMP, NULL),
('1010020300000000000', '1010020000000000000', '/menu/menu2/menu23', 'menu23', '/menu/menu2/menu23/index', NULL, 'Menu', '菜单2-3', '', 0, 0, 0, 1, NULL, 0,1023, 0, CURRENT_TIMESTAMP, NULL);

-- 5. 四级路由（层级4）
INSERT INTO sys_permission(id, parent_id, path, name, component, redirect, icon, title, is_link, is_hide, is_full, is_affix, is_keep_alive, active_menu, no_affix_parent, sort_order, is_deleted, create_time, update_time) VALUES
('1010020201000000000', '1010020200000000000', '/menu/menu2/menu22/menu221', 'menu221', '/menu/menu2/menu22/menu221/index', NULL, 'Menu', '菜单2-2-1', '', 0, 0, 0, 1, NULL, 0,10221, 0, CURRENT_TIMESTAMP, NULL),
('1010020202000000000', '1010020200000000000', '/menu/menu2/menu22/menu222', 'menu222', '/menu/menu2/menu22/menu222/index', NULL, 'Menu', '菜单2-2-2', '', 0, 0, 0, 1, NULL, 0,10222, 0, CURRENT_TIMESTAMP, NULL);


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

