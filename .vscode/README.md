# 🎯 Bamboo 项目 VS Code 开发环境配置指南

## 📋 项目概述

Bamboo 是一个基于前后端分离架构的企业级管理系统，包含以下主要组件：

- **前端 (bamboo-web)**: Vue 3 + Vite + Element Plus + Pinia
- **后端 (bamboo-api)**: Spring Boot 3 + MyBatis Plus + Redis

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
mvn clean install

# 启动服务
mvn spring-boot:run
```

#### 前端服务
```bash
# 进入前端目录
cd bamboo-web

# 安装依赖（首次运行）
pnpm install

# 启动开发服务器
pnpm dev
```

## 🔧 开发环境要求

### 基础环境
- **Node.js**: >= 16.18.0
- **Java**: JDK 17
- **Maven**: 3.6+
- **pnpm**: 最新版（推荐）或 npm/yarn

### 推荐工具
- **数据库**: SQLite (开发环境)
- **Redis**: 本地 Redis 服务
- **IDE**: Visual Studio Code

## 🎯 调试功能特性

### 前端调试
- ✅ 断点调试支持
- ✅ Source Maps 映射
- ✅ 变量监视和求值
- ✅ 调用堆栈查看
- ✅ 热重载支持

### 后端调试
- ✅ Java 断点调试
- ✅ 控制台日志输出
- ✅ 异常堆栈跟踪
- ✅ 内存和性能监控
- ✅ Spring Boot Actuator 集成

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
1. 确保 Java 17 和 Node.js 版本符合要求
2. 首次运行需安装所有依赖
3. 检查端口占用情况（默认 8080 和 5173）

### 调试配置
1. 后端使用 internalConsole 模式，支持变量求值
2. 前端在 integratedTerminal 中运行，便于查看输出
3. 已禁用网络检查参数，避免调试冲突

### 性能优化
1. 启用了跳过 node_modules 的调试优化
2. 配置了合理的超时时间
3. 支持增量编译和热重载

## 🔧 故障排除

### 常见问题

**Q: 后端启动失败**
- 检查 Java 版本是否为 17
- 确认 Maven 配置正确
- 查看控制台错误信息

**Q: 前端无法启动**
- 执行 `pnpm install` 安装依赖
- 检查 Node.js 版本
- 确认端口未被占用

**Q: 调试断点不生效**
- 确认在正确的文件中设置断点
- 检查 Source Maps 是否启用
- 重启调试会话

### 日志查看
- 后端日志：调试控制台输出
- 前端日志：集成终端输出
- 系统日志：各组件的日志文件

---

> 💡 **提示**: 建议使用 VS Code 的一键调试功能，可以大大提高开发效率！