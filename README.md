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

- **JDK 21** (推荐) 或 JDK 8+
- **Node.js 16+**
- **MySQL 8.0+** 或 **SQLite 3.35+**
- **Redis 5.0+**

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
    # MySQL 配置
    url: jdbc:mysql://localhost:3306/bamboo?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: root
    password: your_password
    
    # 或使用 SQLite (开发环境推荐)
    # url: jdbc:sqlite:./data/bamboo.db
    # driver-class-name: org.sqlite.JDBC
```

3. 初始化数据库
```bash
# MySQL 初始化
mysql -u root -p bamboo < src/main/resources/db/auth_by_mysql.sql

# SQLite 初始化 (自动创建表结构)
# 无需手动执行SQL脚本
```

4. 编译项目
```bash
# 清理并编译
mvn clean compile

# 跳过测试编译
mvn clean compile -DskipTests
```

5. 启动应用
```bash
# 开发环境启动
mvn spring-boot:run

# 生产环境启动
mvn spring-boot:run -Dspring.profiles.active=pro

# 或者运行打包后的jar
java -jar target/bamboo-api-0.0.1-SNAPSHOT.jar
```

### 前端部署

1. 安装依赖
```bash
cd bamboo-web

# 使用 pnpm (推荐)
pnpm install

# 或使用 npm
npm install

# 或使用 yarn
yarn install
```

2. 启动开发服务器
```bash
# 开发模式
pnpm dev

# 指定端口启动
pnpm dev --port 3000
```

3. 构建生产版本
```bash
# 生产构建
pnpm build

# 预览生产构建
pnpm preview

# 构建并预览
pnpm build && pnpm preview
```

## 📁 项目结构

```
bamboo/
├── .vscode/                      # VS Code 配置
│   ├── launch.json              # 调试配置
│   ├── settings.json            # 工作区设置
│   ├── extensions.json          # 推荐插件
│   └── README.md                # VS Code 开发指南
├── bamboo-api/                  # 后端服务
│   ├── src/main/java/com/nutcracker/bamboo/
│   │   ├── application/         # 应用层（Application Layer）
│   │   │   ├── model/           # 应用模型
│   │   │   │   ├── command/     # 命令对象
│   │   │   │   ├── dto/         # 数据传输对象
│   │   │   │   ├── query/       # 查询对象
│   │   │   │   └── response/    # 响应对象
│   │   │   └── service/         # 应用服务接口
│   │   │       ├── auth/        # 认证授权服务
│   │   │       ├── biz/         # 业务服务
│   │   │       ├── impl/        # 加密策略实现
│   │   │       ├── secret/      # 密钥服务
│   │   │       ├── BaseStrategy.java
│   │   │       └── StrategyFactory.java
│   │   ├── common/              # 公共模块
│   │   │   ├── annotation/      # 自定义注解
│   │   │   ├── constant/        # 常量定义
│   │   │   ├── enums/           # 枚举类
│   │   │   ├── exception/       # 全局异常
│   │   │   │   ├── BusinessException.java
│   │   │   │   ├── DomainException.java
│   │   │   │   └── SecurityException.java
│   │   │   ├── util/            # 工具类
│   │   │   │   ├── salt/        # 加密工具
│   │   │   │   ├── secret/      # 密钥工具
│   │   │   │   ├── BigDecimalUtil.java
│   │   │   │   ├── IPUtils.java
│   │   │   │   ├── JSON.java
│   │   │   │   └── ...
│   │   │   └── wrapper/         # 响应包装类
│   │   ├── config/              # 应用配置（已提升到根包）
│   │   │   ├── async/           # 异步配置
│   │   │   ├── cache/           # 缓存配置
│   │   │   ├── captcha/         # 验证码配置
│   │   │   ├── codegen/         # 代码生成配置
│   │   │   ├── messaging/       # 消息通信配置
│   │   │   ├── openapi/         # API文档配置
│   │   │   ├── security/        # 安全配置
│   │   │   ├── web/             # Web配置
│   │   │   └── wx/              # 微信小程序配置
│   │   ├── domain/              # 领域层（Domain Layer）
│   │   │   └── model/           # 领域模型
│   │   │       ├── command/     # 领域命令
│   │   │       ├── entity/      # 领域实体
│   │   │       └── valueobject/ # 值对象
│   │   │   ├── repository/      # 仓储接口（已独立）
│   │   │   └── service/         # 领域服务（已独立）
│   │   ├── infrastructure/      # 基础设施层（已扁平化）
│   │   │   ├── converter/       # 对象转换器
│   │   │   │   └── auth/        # 认证转换器
│   │   │   ├── entity/          # 数据库实体
│   │   │   │   ├── auth/        # 认证实体
│   │   │   │   └── biz/         # 业务实体
│   │   │   └── mapper/          # MyBatis Mapper
│   │   │       ├── auth/        # 认证Mapper
│   │   │       ├── biz/         # 业务Mapper
│   │   │       └── CustomDateTypeHandler.java
│   │   └── web/                 # Web层（Presentation Layer）
│   │       ├── aop/             # AOP切面
│   │       ├── filter/          # 过滤器
│   │       ├── rest/            # REST控制器
│   │       ├── security/        # 安全配置
│   │       │   ├── exception/   # 安全异常处理
│   │       │   ├── extension/   # 认证扩展
│   │       │   │   ├── sms/     # 短信认证
│   │       │   │   └── wx/      # 微信认证
│   │       │   ├── filter/      # 安全过滤器
│   │       │   ├── service/     # 安全服务
│   │       │   └── util/        # 安全工具
│   │       └── validator/       # 参数校验
│   └── src/main/resources/
│       ├── db/                  # 数据库脚本
│       ├── mapper/              # MyBatis映射文件
│       └── application*.yml     # 配置文件
├── bamboo-web/                  # 前端页面
│   ├── src/
│   │   ├── api/                 # API接口
│   │   ├── components/          # 组件库
│   │   ├── layouts/             # 页面布局
│   │   ├── routers/             # 路由配置
│   │   ├── stores/              # 状态管理
│   │   ├── views/               # 页面视图
│   │   └── utils/               # 工具函数
│   ├── config/                  # 配置文件
│   └── package.json
└── logs/                        # 日志文件目录
```

## 🏗️ 架构设计

### DDD分层架构

本项目采用领域驱动设计（DDD）的分层架构模式：

**1. 应用层（Application Layer）**
- 负责业务流程编排和用例实现
- 包含应用服务接口和实现
- 处理DTO转换和事务管理

**2. 领域层（Domain Layer）**
- 核心业务逻辑和规则
- 包含领域实体、值对象、聚合根
- 定义仓储接口和领域服务

**3. 基础设施层（Infrastructure Layer）**
- 技术实现细节
- 数据库访问、消息队列、外部服务调用
- 仓储接口的具体实现

**4. 表现层（Presentation Layer）**
- 用户界面和API接口
- 控制器、过滤器、安全配置
- 参数校验和异常处理

### 包结构说明

```
com.nutcracker.bamboo
├── application/     # 应用层 - 业务流程编排
├── common/          # 公共模块 - 共享组件和异常处理
├── config/          # 应用配置 - 各种配置类（已提升到根包）
├── domain/          # 领域层 - 核心业务逻辑
│   ├── model/       # 领域模型 - 实体、值对象、命令
│   ├── repository/  # 仓储接口 - 数据访问契约（已独立）
│   └── service/     # 领域服务 - 跨聚合业务逻辑（已独立）
├── infrastructure/  # 基础设施层 - 技术实现（已扁平化）
│   ├── converter/   # 对象转换器 - DO与Domain对象映射
│   ├── entity/      # 数据库实体 - ORM映射对象
│   └── mapper/      # MyBatis映射器 - 数据库访问接口
└── web/             # 表现层 - 接口和控制器
```

**关键优化点**：
- `config` 包已从 `infrastructure.config` 提升到根包级别
- `domain.repository` 和 `domain.service` 已从 `domain.model` 中独立出来
- `infrastructure.persistence` 已扁平化为 `infrastructure` 直接包含 converter/entity/mapper

## 🔧 配置说明

### 环境配置

后端支持多种环境配置：
- `application-dev.yml`：开发环境
- `application-pro.yml`：生产环境
- `application.yml`：基础配置

### 数据库配置

支持两种数据库模式：

**MySQL 模式**：
```yaml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/bamboo?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:password}
```

**SQLite 模式**（开发推荐）：
```yaml
spring:
  datasource:
    driver-class-name: org.sqlite.JDBC
    url: jdbc:sqlite:./data/bamboo.db
```

### Redis 配置
```yaml
spring:
  redis:
    host: ${REDIS_HOST:localhost}
    port: ${REDIS_PORT:6379}
    password: ${REDIS_PASSWORD:}
    database: ${REDIS_DATABASE:0}
    timeout: 2000ms
    lettuce:
      pool:
        max-active: 8
        max-wait: -1ms
        max-idle: 8
        min-idle: 0
```

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
FROM openjdk:21-jre-slim

WORKDIR /app

# 复制jar文件
COPY target/bamboo-api-0.0.1-SNAPSHOT.jar app.jar

# 暴露端口
EXPOSE 8080

# 启动应用
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
```

### Docker Compose部署

```yaml
# docker-compose.yml
version: '3.8'
services:
  bamboo-api:
    build: ./bamboo-api
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=docker
      - DB_HOST=mysql
      - REDIS_HOST=redis
    depends_on:
      - mysql
      - redis
    networks:
      - bamboo-network

  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: bamboo
    ports:
      - "3306:3306"
    volumes:
      - mysql-data:/var/lib/mysql
      - ./bamboo-api/src/main/resources/db:/docker-entrypoint-initdb.d
    networks:
      - bamboo-network

  redis:
    image: redis:alpine
    ports:
      - "6379:6379"
    networks:
      - bamboo-network

volumes:
  mysql-data:

networks:
  bamboo-network:
    driver: bridge
```

### 启动命令

```bash
# 构建并启动所有服务
docker-compose up -d

# 查看服务状态
docker-compose ps

# 查看日志
docker-compose logs -f bamboo-api

# 停止服务
docker-compose down
```

## 🔒 安全特性

- **身份认证**：JWT Token无状态认证
- **权限控制**：基于注解的方法级权限验证
- **数据安全**：敏感信息加密存储
- **传输安全**：HTTPS支持
- **防护机制**：防XSS、CSRF、SQL注入攻击

## 📊 API文档

### Swagger UI
启动后访问：`http://localhost:8080/swagger-ui.html`

### 主要API端点

**认证相关**：
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/auth/captcha` - 获取验证码
- `POST /api/auth/refresh` - 刷新Token

**用户管理**：
- `GET /api/users` - 用户列表
- `POST /api/users` - 创建用户
- `PUT /api/users/{id}` - 更新用户
- `DELETE /api/users/{id}` - 删除用户

**角色管理**：
- `GET /api/roles` - 角色列表
- `POST /api/roles` - 创建角色
- `PUT /api/roles/{id}` - 更新角色
- `DELETE /api/roles/{id}` - 删除角色

**菜单管理**：
- `GET /api/menus` - 菜单列表
- `POST /api/menus` - 创建菜单
- `PUT /api/menus/{id}` - 更新菜单
- `DELETE /api/menus/{id}` - 删除菜单

### Postman 集合
项目根目录提供 Postman 集合文件，可直接导入使用。

## 🤝 贡献指南

### 开发流程

1. **Fork 项目**到您的GitHub账户
2. **克隆到本地**：
   ```bash
   git clone https://github.com/your-username/bamboo.git
   cd bamboo
   ```
3. **创建功能分支**：
   ```bash
   git checkout -b feature/amazing-feature
   ```
4. **开发并测试**功能
5. **提交更改**：
   ```bash
   git add .
   git commit -m 'feat: add amazing feature'
   ```
6. **推送到远程**：
   ```bash
   git push origin feature/amazing-feature
   ```
7. **创建 Pull Request**

### 代码规范

**后端规范**：
- 遵循阿里巴巴Java开发手册
- 使用统一的代码格式化配置
- 添加必要的JavaDoc注释
- 编写单元测试

**前端规范**：
- 使用ESLint + Prettier统一代码风格
- 遵循Vue 3 Composition API最佳实践
- 组件命名采用PascalCase
- 文件命名采用kebab-case

**Git提交规范**：
```
type(scope): subject

body(optional)

footer(optional)
```

类型说明：
- `feat`: 新功能
- `fix`: 修复bug
- `docs`: 文档更新
- `style`: 代码格式调整
- `refactor`: 代码重构
- `test`: 测试相关
- `chore`: 构建过程或辅助工具变动

## 📈 性能监控

### 系统监控指标

**应用性能**：
- JVM内存使用情况
- 线程池状态
- 数据库连接池
- HTTP请求响应时间

**业务监控**：
- 用户活跃度统计
- API调用频率
- 错误率统计
- 缓存命中率

### 监控配置
```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

## 🔧 常见问题

### 开发环境问题

**Q: 后端启动时报数据库连接错误？**
A: 检查数据库服务是否启动，配置文件中的连接信息是否正确

**Q: 前端页面空白或报错？**
A: 确认后端API服务已启动，检查跨域配置

**Q: Redis连接超时？**
A: 确认Redis服务运行正常，防火墙设置正确

### 部署问题

**Q: Docker容器启动失败？**
A: 检查端口占用情况，确认镜像构建成功

**Q: 生产环境性能差？**
A: 调整JVM参数，优化数据库索引，增加缓存策略

## 📚 学习资源

### 官方文档
- [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Vue 3 Documentation](https://vuejs.org/guide/introduction.html)
- [Element Plus Components](https://element-plus.org/en-US/component/button.html)

### 教程推荐
- Spring Security实战教程
- Vue 3 Composition API深度解析
- DDD领域驱动设计实践
- 微服务架构设计模式

### 社区资源
- Stack Overflow相关技术问答
- GitHub开源项目参考
- 技术博客和文章

## 📄 许可证

本项目采用木兰宽松许可证（MulanPSL2）- 查看 [LICENSE](LICENSE) 文件了解详情

## 👥 团队介绍

### 核心团队成员

- **架构师**：负责系统整体架构设计和DDD领域建模
- **后端开发**：Spring Boot 3 + Spring Security + MyBatis Plus开发
- **前端开发**：Vue 3 + Vite + Element Plus + Pinia开发
- **DevOps工程师**：CI/CD流水线和容器化部署
- **测试工程师**：自动化测试和质量保证
- **UI/UX设计师**：界面设计和用户体验优化

### 技术栈负责人

- **Java技术栈**：@java-developer
- **Vue技术栈**：@vue-developer
- **数据库**：@db-admin
- **运维部署**：@devops-engineer

## 🙏 致谢

### 核心框架
- [Spring Boot](https://spring.io/projects/spring-boot) - 后端核心框架
- [Vue.js](https://vuejs.org/) - 前端核心框架
- [Element Plus](https://element-plus.org/) - UI组件库
- [MyBatis Plus](https://baomidou.com/) - ORM框架

### 工具和库
- [Vite](https://vitejs.dev/) - 前端构建工具
- [Pinia](https://pinia.vuejs.org/) - 状态管理
- [Redis](https://redis.io/) - 缓存数据库
- [JWT](https://jwt.io/) - Token认证
- [Lombok](https://projectlombok.org/) - Java代码简化
- [MapStruct](https://mapstruct.org/) - 对象映射

### 开发工具
- [VS Code](https://code.visualstudio.com/) - 主要IDE
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) - Java开发
- [Postman](https://www.postman.com/) - API测试
- [Docker](https://www.docker.com/) - 容器化部署

## 🚀 路线图

### 短期计划 (1-3个月)
- [ ] 完善单元测试覆盖率
- [ ] 优化前端性能和用户体验
- [ ] 增加更多图表展示功能
- [ ] 完善文档和示例代码

### 中期计划 (3-6个月)
- [ ] 支持多租户架构
- [ ] 集成消息通知系统
- [ ] 增加工作流引擎
- [ ] 支持插件化扩展

### 长期计划 (6-12个月)
- [ ] 微服务架构改造
- [ ] 支持多语言国际化
- [ ] 移动端APP开发
- [ ] AI智能推荐功能

## 🤝 社区参与

### 如何参与
1. **报告Bug**：在GitHub Issues中提交问题
2. **功能建议**：提出新功能需求和改进意见
3. **代码贡献**：提交Pull Request修复问题或添加功能
4. **文档完善**：帮助改进项目文档
5. **社区讨论**：参与技术讨论和经验分享

### 贡献奖励
- 优秀贡献者将获得项目纪念品
- 核心贡献者可获得项目署名权
- 持续贡献者有机会成为项目维护者

---

> 💪 **Bamboo - 让权限管理变得更简单！**
> 
> 📧 **联系我们**：如有任何问题或建议，请通过GitHub Issues与我们联系
> 
> 🌟 **喜欢这个项目吗？** 请给我们一个Star！