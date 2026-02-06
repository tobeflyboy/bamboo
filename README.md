# Bamboo 权限管理系统

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.5.8-brightgreen" alt="Spring Boot Version">
  <img src="https://img.shields.io/badge/Vue.js-3.4.46-blue" alt="Vue.js Version">
  <img src="https://img.shields.io/badge/License-MulanPSL2-orange" alt="License">
</p>

## 📋 项目简介

Bamboo 是一个基于 Spring Boot 和 Vue.js 开发的企业级权限管理系统，采用前后端分离架构设计。系统提供了完善的用户认证、权限控制、菜单管理等功能，适用于各类企业应用开发。

### 核心特性

- 🔐 **RBAC权限模型**：基于角色的访问控制，灵活的权限配置
- 🎨 **现代化界面**：采用Element Plus组件库，响应式设计
- 📱 **多端适配**：支持PC端和移动端访问
- ⚡ **高性能**：Redis缓存优化，数据库连接池管理
- 🛡️ **安全防护**：JWT认证、XSS防护、SQL注入防护
- 🌍 **国际化**：支持多语言切换
- 📊 **数据可视化**：集成ECharts图表组件

## 🏗️ 技术架构

### 后端技术栈
- **核心框架**：Spring Boot 3.5.8
- **安全框架**：Spring Security + JWT
- **持久层**：MyBatis Plus
- **数据库**：MySQL/SQLite
- **缓存**：Redis
- **API文档**：Swagger/OpenAPI

### 前端技术栈
- **核心框架**：Vue 3 + Composition API
- **构建工具**：Vite
- **UI框架**：Element Plus
- **状态管理**：Pinia
- **路由管理**：Vue Router 4
- **HTTP客户端**：Axios
- **代码规范**：ESLint + Prettier

## 🚀 快速开始

### 环境要求

- JDK 8+
- Node.js 16+
- MySQL 5.7+/SQLite
- Redis 5.0+

### 后端部署

1. 克隆项目
```bash
git clone https://github.com/your-username/bamboo.git
cd bamboo/bamboo-api
```

2. 配置数据库
```yaml
# application-dev.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/bamboo?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: your_password
```

3. 初始化数据库
```bash
# 执行SQL脚本
mysql -u root -p bamboo < src/main/resources/db/auth_by_mysql.sql
```

4. 启动应用
```bash
mvn spring-boot:run
# 或者
java -jar target/bamboo-api-1.0.0.jar
```

### 前端部署

1. 安装依赖
```bash
cd bamboo-web
pnpm install
```

2. 启动开发服务器
```bash
pnpm dev
```

3. 构建生产版本
```bash
pnpm build
```

## 📁 项目结构

```
bamboo/
├── bamboo-api/              # 后端服务
│   ├── src/main/java/com/nutcracker/bamboo/
│   │   ├── application/     # 应用服务层
│   │   ├── domain/          # 领域模型
│   │   ├── infrastructure/  # 基础设施层
│   │   ├── interfaces/      # 接口层
│   │   └── shared/          # 共享模块
│   └── src/main/resources/
│       ├── db/              # 数据库脚本
│       ├── mapper/          # MyBatis映射文件
│       └── application*.yml # 配置文件
└── bamboo-web/              # 前端页面
    ├── src/
    │   ├── api/             # API接口
    │   ├── components/      # 组件库
    │   ├── layouts/         # 页面布局
    │   ├── routers/         # 路由配置
    │   ├── stores/          # 状态管理
    │   ├── views/           # 页面视图
    │   └── utils/           # 工具函数
    └── package.json
```

## 🔧 配置说明

### 环境配置

后端支持多种环境配置：
- `application-dev.yml`：开发环境
- `application-pro.yml`：生产环境
- `application.yml`：基础配置

### 主要配置项

```yaml
# 服务器配置
server:
  port: 8080

# 数据源配置
spring:
  datasource:
    type: com.zaxxer.hikari.HikariDataSource
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/bamboo
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}

# Redis配置
  redis:
    host: localhost
    port: 6379
    password: ${REDIS_PASSWORD:}
    database: 0

# JWT配置
jwt:
  secret: ${JWT_SECRET:your-secret-key}
  expiration: 86400
```

## 🎯 功能模块

### 用户认证
- 用户登录/登出
- JWT Token管理
- 密码加密存储
- 登录失败锁定机制

### 权限管理
- 用户管理
- 角色管理
- 菜单权限配置
- 按钮级别权限控制

### 系统监控
- 操作日志记录
- 系统性能监控
- 在线用户统计
- 异常日志追踪

### 数据管理
- 字典数据维护
- 系统参数配置
- 文件上传管理
- 数据导入导出

## 🐳 Docker部署

### 后端容器化

```dockerfile
# bamboo-api/Dockerfile
FROM openjdk:8-jre-alpine
COPY target/bamboo-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### Docker Compose部署

```bash
# 使用docker-compose启动完整服务
docker-compose -f api-docker-compose.yml up -d
```

## 🔒 安全特性

- **身份认证**：JWT Token无状态认证
- **权限控制**：基于注解的方法级权限验证
- **数据安全**：敏感信息加密存储
- **传输安全**：HTTPS支持
- **防护机制**：防XSS、CSRF、SQL注入攻击

## 📊 API文档

启动后访问：`http://localhost:8080/swagger-ui.html`

主要API端点：
- `/api/auth/login` - 用户登录
- `/api/users` - 用户管理
- `/api/roles` - 角色管理
- `/api/menus` - 菜单管理

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

### 代码规范

- 后端：遵循阿里巴巴Java开发手册
- 前端：使用ESLint + Prettier统一代码风格
- Git提交：遵循Angular提交规范

## 📄 许可证

本项目采用木兰宽松许可证（MulanPSL2）- 查看 [LICENSE](LICENSE) 文件了解详情

## 👥 团队介绍

- **架构师**：负责系统整体架构设计
- **后端开发**：Spring Boot + MyBatis开发
- **前端开发**：Vue 3 + Element Plus开发
- **测试工程师**：自动化测试和质量保证

## 🙏 致谢

感谢以下开源项目的支持：
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vue.js](https://vuejs.org/)
- [Element Plus](https://element-plus.org/)
- [MyBatis Plus](https://baomidou.com/)

---
> 💪 **Bamboo - 让权限管理变得更简单！**