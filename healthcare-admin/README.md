# 企微健康社群管理后台系统

## 项目概述

基于企业微信生态的健康管理服务平台管理后台，支持4R角色（家庭医生、健康管家、权益顾问、保险顾问）协同服务。

## 项目结构

```
healthcare-admin/
├── pom.xml                           # 父POM
├── healthcare-admin-common/          # 公共模块
│   ├── src/main/java/com/healthcare/admin/common/
│   │   ├── result/                   # 统一响应结果
│   │   │   ├── Result.java
│   │   │   ├── ResultCode.java
│   │   │   └── PageResult.java
│   │   ├── exception/                # 异常处理
│   │   │   └── BusinessException.java
│   │   ├── enums/                    # 枚举类
│   │   │   └── UserStatus.java
│   │   ├── constant/                 # 常量定义
│   │   └── utils/                    # 工具类
│   └── pom.xml
│
├── healthcare-admin-boot/            # 启动模块（整合所有功能）
│   ├── src/main/java/com/healthcare/admin/boot/
│   │   ├── HealthcareAdminApplication.java
│   │   ├── entity/                   # 实体类
│   │   │   └── SysUser.java
│   │   ├── mapper/                   # 数据访问层
│   │   │   └── SysUserMapper.java
│   │   ├── service/                  # 服务层
│   │   │   └── AuthService.java
│   │   ├── dto/                      # 数据传输对象
│   │   │   └── LoginDTO.java
│   │   ├── vo/                       # 视图对象
│   │   │   └── LoginVO.java
│   │   └── controller/               # 控制器
│   ├── src/main/resources/
│   │   └── application.yml           # 应用配置
│   └── pom.xml
│
└── README.md
```

## 技术栈

### 后端
- Java 17
- Spring Boot 3.2.x
- Spring Security 6.x
- MyBatis Plus 3.5.x
- MySQL 8.0
- Redis 7.x
- JWT 0.12.x
- Knife4j 4.x (API文档)

### 前端
- Vue.js 3.4.x
- TypeScript 5.x
- Element Plus 2.5.x
- Pinia 2.x
- Vite 5.x

## 快速开始

### 1. 数据库初始化

```sql
CREATE DATABASE healthcare_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
```

执行 `deploy/sql/schema.sql` 创建表结构。

### 2. 配置修改

修改 `healthcare-admin-boot/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/healthcare_admin?useUnicode=true&characterEncoding=utf-8
    username: your_username
    password: your_password
  
  redis:
    host: localhost
    port: 6379
```

### 3. 编译运行

```bash
# 编译
mvn clean install

# 运行
cd healthcare-admin-boot
mvn spring-boot:run
```

### 4. 访问

- 应用地址：http://localhost:8081
- API文档：http://localhost:8081/doc.html

## 核心功能模块

### 1. 用户权限管理
- [x] 统一响应结果封装
- [x] 异常处理机制
- [x] 用户登录/登出
- [ ] 角色管理
- [ ] 权限管理
- [ ] 组织架构管理

### 2. 社群管理
- [ ] 社群CRUD
- [ ] 成员管理
- [ ] 关键词回复
- [ ] 内容管理

### 3. API管理
- [ ] API信息维护
- [ ] 在线测试
- [ ] 文档生成
- [ ] 调用统计

### 4. 系统管理
- [ ] 系统配置
- [ ] 文件管理
- [ ] 日志管理
- [ ] 数据统计

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发手册
- 使用Lombok简化代码
- 统一异常处理
- 单元测试覆盖率≥80%

### Git规范
- 分支管理：main / develop / feature/* / hotfix/*
- 提交格式：`type(scope): subject`
  - feat: 新功能
  - fix: 修复
  - docs: 文档
  - refactor: 重构
  - test: 测试

## 接口规范

### 响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": {},
    "timestamp": "2026-02-05 10:00:00",
    "requestId": "req_abc123"
}
```

### 错误码
- 200: 成功
- 400: 请求参数错误
- 401: 未授权
- 403: 禁止访问
- 404: 资源不存在
- 500: 系统错误

## 文档

- [产品需求文档(PRD)](../企微健康社群管理后台系统-PRD.md)
- [开发文档](../企微健康社群管理后台系统-开发文档.md)

## 下一步开发计划

1. 完善用户管理模块（角色、权限、部门）
2. 实现社群管理模块
3. 实现API管理模块
4. 开发前端管理界面
5. 集成企业微信API
6. 实现数据统计功能

## 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 许可证

[MIT](LICENSE)
