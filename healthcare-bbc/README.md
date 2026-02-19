# 企微健康社群 (Healthcare BBC)

基于企业微信生态，构建 "医生 + AI + 保险" 三位一体的健康管理服务平台。

## 1. 项目概述
本项目旨在通过 BBC (Business to Business to Customer) 模式，连接保险机构、医疗服务提供者和终端客户，提供全方位的健康管理服务。

- **文档地址**:
    - [产品需求文档](../企微健康社群方案%20-%20产品需求文档(PRD).md)
    - [开发文档](../企微健康社群方案%20-%20开发文档.md)
    - [项目计划](./PROJECT_PLAN.md)

## 2. 团队角色与分工 (Team Roles)

| 角色 | 职责 | 人员 |
| --- | --- | --- |
| **项目经理 (PM)** | 整体进度把控，风险管理，跨部门协调 | TBD |
| **产品经理 (PO)** | 需求分析，PRD 维护，验收测试 | TBD |
| **架构师** | 技术选型，架构设计，核心难点攻关 | TBD |
| **后端开发** | API 接口开发，数据库设计，AI 服务对接 | TBD |
| **前端开发** | 企微侧边栏/H5 开发，后台管理系统开发 | TBD |
| **测试工程师** | 用例编写，功能测试，性能测试 | TBD |
| **DevOps** | CI/CD 流水线搭建，服务器运维 | TBD |

## 3. 技术栈 (Tech Stack)

### 后端 (Backend)
- **核心框架**: Spring Boot 2.7
- **微服务**: Spring Cloud Alibaba (Nacos, Sentinel)
- **数据库**: MySQL 8.0, Redis 6.0
- **ORM**: MyBatis-Plus
- **工具**: Lombok, Swagger/OpenAPI

### 前端 (Frontend)
- **框架**: Vue 3 + TypeScript
- **构建工具**: Vite
- **UI 组件库**: Element Plus (后台), Vant/TDesign (移动端)
- **状态管理**: Pinia

### 基础设施 (Infra)
- **CI/CD**: GitHub Actions / Jenkins
- **部署**: Docker, Docker Compose, Kubernetes
- **云平台**: Oracle Cloud, Railway, Render

## 4. 快速开始 (Quick Start)

### 4.1 环境准备
- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Redis 6.0+

### 4.2 数据库初始化
1. 本地安装 MySQL 8.0。
2. 创建数据库 `healthcare_bbc`: `CREATE DATABASE healthcare_bbc DEFAULT CHARACTER SET utf8mb4;`。
3. 执行初始化脚本: [schema.sql](./deploy/sql/schema.sql)。
4. 本地安装并启动 Redis 6.0。

### 4.3 启动后端
1. 检查 `backend/src/main/resources/application.yml` 中的数据库及 Redis 连接配置。
2. 运行后端项目:
```bash
cd backend
mvn spring-boot:run
```

### 4.4 启动前端
```bash
cd frontend
npm install
npm run dev
```

## 5. 部署 (Deployment)

### 本地开发
```bash
cd deploy
docker compose up -d
```

### 生产环境
详见 [部署文档](../deploy/QUICKSTART.md)

**支持平台**：
- ✅ Oracle Cloud (永久免费)
- ✅ Railway (一键部署)
- ✅ Render (Blueprint 部署)
- ✅ 任意 Docker 环境

## 6. 开发规范
- **分支管理**: 遵循 Git Flow (main, develop, feature/*, release/*, hotfix/*)
- **代码规范**:
    - Java: 阿里巴巴 Java 开发手册
    - Frontend: ESLint + Prettier
- **提交规范**: Conventional Commits (feat, fix, docs, style, refactor, test, chore)
