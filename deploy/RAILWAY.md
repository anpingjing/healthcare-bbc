# 🚄 Railway 部署完整指南

## 方案概述

Railway 是最简单的部署方式，无需配置服务器，一键自动部署。

**免费额度**：$5/月（约 500 小时运行时间）
**适合场景**：测试、演示、小型生产环境

---

## 📋 部署前准备

### 1. 准备 GitHub 仓库

```bash
# 在项目根目录初始化 git（如果还没有）
cd D:\traecn\企业微信项目

# 初始化 git
git init

# 创建 .gitignore（项目根目录）
echo "node_modules/
target/
*.log
.env
.DS_Store
.idea/
.vscode/" > .gitignore

# 添加所有文件
git add .

# 提交
git commit -m "Initial commit with Railway deployment"

# 创建 GitHub 仓库并推送
# 访问 https://github.com/new 创建新仓库
# 然后执行：
git remote add origin https://github.com/你的用户名/healthcare-bbc.git
git branch -M main
git push -u origin main
```

### 2. 准备环境变量

复制并编辑环境变量文件：

```bash
cd deploy
copy .env.example .env
```

**必填环境变量**：

| 变量名 | 说明 | 获取方式 |
|--------|------|----------|
| `WECHAT_CORP_ID` | 企业微信 Corp ID | 企业微信后台 |
| `WECHAT_CORP_SECRET` | 企业微信密钥 | 企业微信后台 |
| `WECHAT_AGENT_ID` | 应用 ID | 企业微信后台 |
| `WECHAT_TOKEN` | 回调 Token | 自定义 |
| `WECHAT_ENCODING_AES_KEY` | AES 密钥 | 企业微信后台生成 |
| `JWT_SECRET` | JWT 密钥 | `openssl rand -base64 32` |
| `MYSQL_ROOT_PASSWORD` | MySQL 根密码 | 自定义强密码 |
| `MYSQL_PASSWORD` | MySQL 用户密码 | 自定义强密码 |

### 3. 获取企业微信参数

1. 登录 https://work.weixin.qq.com/
2. **管理后台** → **应用管理** → **自建应用**
3. 创建一个新应用或选择现有应用
4. 查看并复制：
   - **Corp ID**（我的企业 → 企业信息）
   - **Secret**（应用详情 → Secret）
   - **Agent ID**（应用详情 → AgentId）

5. 配置回调 URL（稍后填写 Railway 生成的域名）：
   - **接收消息设置** → 配置服务器
   - Token：自定义（如 `healthcare2026`）
   - EncodingAESKey：点击随机生成

---

## 🚀 开始部署

### 步骤 1：登录 Railway

1. 访问 https://railway.com/
2. 点击 **"Login"** → 使用 GitHub 账号登录
3. 登录后进入 Dashboard

### 步骤 2：创建新项目

1. 点击 **"New Project"**
2. 选择 **"Deploy from GitHub repo"**
3. 选择你的 `healthcare-bbc` 仓库
4. Railway 会自动识别 Dockerfile

### 步骤 3：配置服务

Railway 会自动创建服务，你需要配置：

#### 3.1 添加 MySQL 插件

1. 点击项目 → **"New"** → **"Database"** → **"MySQL"**
2. Railway 会自动创建 MySQL 实例
3. 等待 MySQL 部署完成（约 1-2 分钟）

#### 3.2 添加 Redis 插件

1. 点击项目 → **"New"** → **"Redis"**
2. Railway 会自动创建 Redis 实例
3. 等待 Redis 部署完成

#### 3.3 配置主服务

1. 点击你的服务（从 GitHub 部署的那个）
2. 进入 **"Settings"** 标签页

**配置 Build**：
- **Root Directory**: 留空（使用项目根目录）
- **Build Command**: 留空（使用 Dockerfile）
- **Start Command**: 留空（使用 Dockerfile CMD）

**配置环境变量**：

点击 **"Variables"** → 添加以下变量：

```bash
# 数据库配置（Railway 会自动提供 MySQL 连接）
DATABASE_URL=${{MySQL.DATABASE_URL}}
MYSQL_HOST=${{MySQL.HOST}}
MYSQL_PORT=${{MySQL.PORT}}
MYSQL_USER=${{MySQL.USERNAME}}
MYSQL_PASSWORD=${{MySQL.PASSWORD}}
MYSQL_DATABASE=${{MySQL.DATABASE}}
MYSQL_ROOT_PASSWORD=你的根密码

# Redis 配置（Railway 会自动提供）
REDIS_URL=${{Redis.REDIS_URL}}
REDIS_HOST=${{Redis.HOST}}
REDIS_PORT=${{Redis.PORT}}
REDIS_PASSWORD=${{Redis.PASSWORD}}

# JWT 配置
JWT_SECRET=生成随机密钥

# 企业微信配置
WECHAT_CORP_ID=wwace533e386c63f72
WECHAT_CORP_SECRET=你的密钥
WECHAT_AGENT_ID=1000007
WECHAT_TOKEN=你的 Token
WECHAT_ENCODING_AES_KEY=你的 AES 密钥

# 其他配置
NODE_ENV=production
SPRING_PROFILES_ACTIVE=prod
PORT=8080
```

**注意**：Railway 使用 `${{Service.VARIABLE}}` 语法引用其他服务的环境变量。

### 步骤 4：配置网络

1. 进入服务 → **"Settings"** → **"Networking"**
2. 点击 **"Generate Domain"**
3. Railway 会生成一个公网域名（如 `healthcare-bbc-production.up.railway.app`）
4. 确保 **Public Networking** 已启用

### 步骤 5：配置健康检查

1. 进入服务 → **"Settings"** → **"Healthcheck"**
2. 配置：
   - **Path**: `/actuator/health`
   - **Timeout**: 30s
   - **Initial Delay**: 60s

### 步骤 6：部署

1. 点击 **"Deploy"** 标签页
2. Railway 会自动开始构建和部署
3. 查看构建日志（约 5-10 分钟）
4. 部署成功后，状态显示 **"Running"**

---

## 🔍 验证部署

### 1. 访问应用

打开浏览器访问 Railway 生成的域名：
- 前端：`https://your-domain.up.railway.app/`
- API: `https://your-domain.up.railway.app/api/`
- Swagger: `https://your-domain.up.railway.app/swagger-ui.html`

### 2. 检查服务状态

在 Railway Dashboard 查看：
- **Deploy** 标签页 → 查看部署日志
- **Metrics** 标签页 → 查看 CPU/内存使用
- **Logs** → 查看实时日志

### 3. 测试 API

```bash
# 健康检查
curl https://your-domain.up.railway.app/actuator/health

# 测试 API
curl https://your-domain.up.railway.app/api/health
```

### 4. 检查数据库连接

在 Railway Dashboard → **"Variables"** 中确认 MySQL 环境变量已正确注入。

---

## ⚙️ 高级配置

### 配置企业微信回调 URL

1. 获取 Railway 域名后（如 `https://healthcare-bbc.up.railway.app`）
2. 登录企业微信后台
3. **应用管理** → **自建应用** → **接收消息设置**
4. 配置：
   - **URL**: `https://healthcare-bbc.up.railway.app/wechat/callback`
   - **Token**: 与 .env 中一致
   - **EncodingAESKey**: 与 .env 中一致
5. 点击 **"保存"**

### 配置自定义域名（可选）

1. Railway Dashboard → **"Settings"** → **"Domains"**
2. 点击 **"Add Custom Domain"**
3. 输入你的域名
4. 按提示配置 DNS（CNAME 记录）
5. Railway 会自动配置 HTTPS

### 配置自动部署

Railway 默认启用自动部署：
- 推送代码到 `main` 分支 → 自动部署
- 可在 **"Settings"** → **"GitHub"** 中配置

---

## 💰 费用说明

### Railway 定价

| 项目 | 免费额度 | 超出后 |
|------|----------|--------|
| 计算 | $5/月 | $0.00000714/秒 |
| 数据库 | $5/月 | $0.125/GB/月 |
| 带宽 | 100GB/月 | $0.10/GB |

### 预估费用

对于小型应用：
- **计算**: ~$5/月（500 小时）
- **MySQL**: ~$1-2/月
- **Redis**: ~$1-2/月
- **总计**: ~$7-9/月

### 优化建议

1. 使用 **Hobby 计划**（$5/月）获得稳定运行
2. 配置 **自动休眠**（非生产环境）
3. 监控资源使用，避免超出额度

---

## 🐛 故障排查

### 部署失败

**查看日志**：
```bash
# Railway Dashboard → Deploy → 查看构建日志
```

**常见问题**：

1. **构建超时**：
   - 检查 Dockerfile 是否正确
   - 确保所有依赖可下载

2. **启动失败**：
   - 检查环境变量是否配置完整
   - 查看运行时日志

3. **数据库连接失败**：
   - 确认 MySQL 插件已添加
   - 检查 DATABASE_URL 是否正确

### 应用无法访问

1. 确认 **Public Networking** 已启用
2. 检查防火墙/安全组
3. 查看日志是否有错误

### 企业微信回调失败

1. 确认回调 URL 使用 HTTPS
2. 检查 Token 和 AES Key 是否匹配
3. 查看日志中的回调请求

---

## 📊 监控和维护

### 查看日志

Railway Dashboard → **"Logs"** 查看实时日志

### 监控资源

Railway Dashboard → **"Metrics"** 查看：
- CPU 使用率
- 内存使用
- 网络流量

### 重启服务

Railway Dashboard → **"Deploy"** → **"Restart"**

### 回滚部署

Railway Dashboard → **"Deploy"** → 选择历史版本 → **"Redeploy"**

---

## 🔄 更新部署

### 自动部署（推荐）

```bash
# 推送代码到 main 分支
git push origin main

# Railway 会自动检测并重新部署
```

### 手动部署

1. Railway Dashboard → **"Deploy"**
2. 点击 **"Redeploy"**

### 更新环境变量

1. Railway Dashboard → **"Variables"**
2. 修改变量
3. 点击 **"Save"** → 自动重新部署

---

## 📝 检查清单

部署前确认：

- [ ] GitHub 仓库已创建并推送代码
- [ ] Railway 账号已登录
- [ ] 企业微信参数已获取
- [ ] .env 文件已配置完整
- [ ] JWT_SECRET 已生成随机值

部署后确认：

- [ ] 服务状态显示 "Running"
- [ ] 可以访问前端页面
- [ ] API 健康检查通过
- [ ] 数据库连接正常
- [ ] 企业微信回调配置完成

---

## 🆘 获取帮助

### Railway 文档

- 官方文档：https://docs.railway.com/
- 社区：https://discord.gg/railway

### 项目文档

- `QUICKSTART.md` - 快速开始
- `DEPLOY.md` - 完整部署文档

---

**祝部署顺利！** 🎉

*最后更新：2026-02-19*
