# 🎯 Bamboo 项目 VS Code 开发环境配置指南

## 📋 项目概述

Bamboo 是一个基于前后端分离架构的企业级权限管理系统，包含以下主要组件：

- **前端 (bamboo-web)**: Vue 3.4.46 + Vite + Element Plus + Pinia
- **后端 (bamboo-api)**: Spring Boot 3.5.8 + MyBatis Plus + Redis + Spring Security

## ⚡ 一键启动配置

### 🚀 调试配置

按 `Ctrl+Shift+D` 打开 VS Code 调试面板，可选择以下配置：

| 配置名称 | 类型 | 描述 |
|---------|------|------|
| **bamboo-api** | Java | 启动 Spring Boot 后端服务 |
| **bamboo-web** | Node.js | 启动 Vue 前端开发服务器 |

### 🛠️ 任务配置

项目包含以下自动化任务：

- **npm: dev** - 启动前端开发服务器 (Vite)
- **Install dependencies** - 安装项目依赖

## 🎯 快速开始

### 🔄 最新变更提醒

> ⚠️ **重要更新**：项目已升级至以下版本
> - Spring Boot: 3.5.8
> - Vue.js: 3.4.46
> - Java: 推荐使用 JDK 21
> - 数据库: 支持 MySQL/SQLite 双模式

### 方法一：VS Code 一键调试（推荐）

1. **启动后端服务**
   - 按 `Ctrl+Shift+D` 打开调试面板
   - 选择 **bamboo-api** 配置
   - 按 `F5` 启动后端服务
   - 服务将在 `http://localhost:8080` 运行

2. **启动前端服务**
   - 在调试面板中选择 **bamboo-web** 配置
   - 按 `F5` 启动前端开发服务器
   - 自动在内置终端中运行并监听文件变化

### 方法二：命令行手动启动

#### 后端服务
```bash
# 进入后端目录
cd bamboo-api

# Maven 清理并编译
mvn clean compile

# 启动服务
mvn spring-boot:run

# 或者跳过测试启动
mvn spring-boot:run -DskipTests
```

#### 前端服务
```bash
# 进入前端目录
cd bamboo-web

# 安装依赖（首次运行）
pnpm install

# 启动开发服务器
pnpm dev

# 或者使用 npm
npm run dev
```

## 🔧 开发环境要求

### 基础环境
- **Node.js**: >= 16.18.0
- **Java**: JDK 21 (推荐)
- **Maven**: 3.6+
- **pnpm**: 最新版（推荐）或 npm/yarn

### 推荐工具
- **数据库**: SQLite (开发环境) 或 MySQL 8.0+
- **Redis**: 本地 Redis 服务 5.0+
- **IDE**: Visual Studio Code (推荐) 或 IntelliJ IDEA
- **数据库管理**: DBeaver (推荐) 或 Navicat

## 🎯 调试功能特性

### 前端调试
- ✅ 断点调试支持
- ✅ Source Maps 映射
- ✅ 变量监视和求值
- ✅ 调用堆栈查看
- ✅ 热重载支持
- ✅ Vue DevTools 集成
- ✅ Pinia 状态调试
- ✅ 组件层次结构查看

### 后端调试
- ✅ Java 断点调试
- ✅ 控制台日志输出
- ✅ 异常堆栈跟踪
- ✅ 内存和性能监控
- ✅ Spring Boot Actuator 集成
- ✅ Lombok 注解支持
- ✅ MapStruct 映射调试

## ⌨️ 常用快捷键

### 调试操作
| 快捷键 | 功能 |
|--------|------|
| `F5` | 启动/继续调试 |
| `Shift+F5` | 停止调试 |
| `F9` | 切换断点 |
| `F10` | 单步跳过 |
| `F11` | 单步进入 |
| `Shift+F11` | 单步跳出 |

### 开发辅助
| 快捷键 | 功能 |
|--------|------|
| `Ctrl+Shift+D` | 打开调试面板 |
| `Ctrl+Shift+X` | 打开扩展面板 |
| `Ctrl+`` | 切换终端 |
| `Ctrl+P` | 快速打开文件 |
| `Ctrl+Shift+B` | 运行构建任务 |
| `F1` | 命令面板 |

## 📁 项目结构

```
bamboo/
├── .vscode/              # VS Code 配置
│   ├── launch.json      # 调试配置
│   ├── tasks.json       # 任务配置
│   ├── settings.json    # 工作区设置
│   └── README.md        # 本文件
├── bamboo-api/          # 后端服务
│   ├── src/main/java/   # Java 源码
│   └── pom.xml          # Maven 配置
└── bamboo-web/          # 前端应用
    ├── src/             # Vue 源码
    └── package.json     # npm 配置
```

## ⚠️ 注意事项

### 环境配置
1. 确保 Java 21 和 Node.js 版本符合要求
2. 首次运行需安装所有依赖
3. 检查端口占用情况（默认 8080 和 5173）
4. 确认数据库服务已启动（MySQL/SQLite）
5. 确认 Redis 服务已启动

### 调试配置
1. 后端使用 internalConsole 模式，支持变量求值
2. 前端在 integratedTerminal 中运行，便于查看输出
3. 已禁用网络检查参数，避免调试冲突

### 性能优化
1. 启用了跳过 node_modules 的调试优化
2. 配置了合理的超时时间
3. 支持增量编译和热重载
4. Java 调试使用 internalConsole 模式提升性能
5. 前端调试禁用网络检查参数避免冲突

## 🔧 故障排除

### 常见问题

**Q: 后端启动失败**
- 检查 Java 版本是否为 21
- 确认 Maven 配置正确
- 查看控制台错误信息
- 检查数据库连接配置

**Q: 前端无法启动**
- 执行 `pnpm install` 安装依赖
- 检查 Node.js 版本
- 确认端口未被占用
- 检查 `.env` 环境配置文件

**Q: 调试断点不生效**
- 确认在正确的文件中设置断点
- 检查 Source Maps 是否启用
- 重启调试会话
- 确认代码已被正确编译

### 日志查看
- 后端日志：调试控制台输出
- 前端日志：集成终端输出
- 系统日志：各组件的日志文件
- 数据库日志：查看相应数据库服务日志
- Redis日志：查看Redis服务日志

---

## 📚 学习资源

### 官方文档
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Vue 3 官方文档](https://vuejs.org/)
- [Element Plus 文档](https://element-plus.org/)
- [MyBatis Plus 文档](https://baomidou.com/)

### 开发工具
- [VS Code 官方文档](https://code.visualstudio.com/docs)
- [Java Extension Pack](https://marketplace.visualstudio.com/items?itemName=vscjava.vscode-java-pack)
- [Vue Language Features (Volar)](https://marketplace.visualstudio.com/items?itemName=Vue.volar)

---

> 💡 **提示**: 建议使用 VS Code 的一键调试功能，可以大大提高开发效率！
> 
> 🎯 **小贴士**: 遇到问题时，先查看调试控制台和终端输出的详细错误信息