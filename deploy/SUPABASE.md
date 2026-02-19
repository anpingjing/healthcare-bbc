# Supabase 部署指南

本项目支持使用 Supabase (PostgreSQL) 作为后端数据库。以下是配置步骤。

## 1. 获取数据库连接信息

您提供的 Supabase 信息如下：

- **URL**: `https://texbpopnfrjnkopnqjzd.supabase.co`
- **Anon Key**: `sb_publishable_yEH6dL1n7hFcIkc3PaT_Zg_Vkh2Y7yb`
- **PostgreSQL 连接串**: `postgresql://postgres:[YOUR-PASSWORD]@db.texbpopnfrjnkopnqjzd.supabase.co:5432/postgres`

## 2. 初始化数据库结构

由于 Supabase 使用 PostgreSQL，而本项目默认使用 MySQL，您需要手动初始化数据库表结构。

1. 登录 [Supabase Dashboard](https://supabase.com/dashboard/project/texbpopnfrjnkopnqjzd/editor/sql)。
2. 进入 **SQL Editor**。
3. 点击 **New Query**。
4. 复制 `deploy/sql/supabase-schema.sql` 文件的内容并粘贴到编辑器中。
5. 点击 **Run** 执行脚本。

## 3. 配置环境变量 (Railway / Docker)

在部署平台（如 Railway）或本地 `.env` 文件中配置以下环境变量：

```bash
# 数据库连接 (注意替换 [YOUR-PASSWORD])
SPRING_DATASOURCE_URL=jdbc:postgresql://db.texbpopnfrjnkopnqjzd.supabase.co:5432/postgres?sslmode=require
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=[YOUR-PASSWORD]
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver

# Redis (Supabase 不提供 Redis，您仍需使用 Railway Redis 或其他服务)
SPRING_REDIS_HOST=...
SPRING_REDIS_PORT=6379
SPRING_REDIS_PASSWORD=...

# 其他配置保持不变
JWT_SECRET=...
WECHAT_CORP_ID=...
```

## 4. 前端配置 (可选)

如果前端需要直接访问 Supabase（例如使用 Supabase Auth 或 Storage），请在前端 `.env` 中添加：

```bash
VITE_SUPABASE_URL=https://texbpopnfrjnkopnqjzd.supabase.co
VITE_SUPABASE_ANON_KEY=sb_publishable_yEH6dL1n7hFcIkc3PaT_Zg_Vkh2Y7yb
```

*注意：目前后端代码主要依赖 Spring Boot + MyBatis Plus，尚未集成 Supabase 客户端 SDK。以上前端配置仅供将来扩展使用。*
