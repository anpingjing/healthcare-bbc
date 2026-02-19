# 🚄 Railway 部署快速检查清单

## 部署前准备

### 1. GitHub 仓库
- [ ] 代码已推送到 GitHub
- [ ] 主分支为 `main`
- [ ] `deploy/Dockerfile.railway` 存在

### 2. 企业微信参数
- [ ] Corp ID: `____________________`
- [ ] Corp Secret: `____________________`
- [ ] Agent ID: `____________________`
- [ ] Token: `____________________`
- [ ] EncodingAESKey: `____________________`

### 3. 密码配置
- [ ] MySQL Root 密码：`____________________`
- [ ] MySQL 用户密码：`____________________`
- [ ] Redis 密码：`____________________`
- [ ] JWT Secret：`____________________`

---

## Railway 部署步骤

### 步骤 1：登录 Railway
- [ ] 访问 https://railway.com/
- [ ] 使用 GitHub 账号登录

### 步骤 2：创建项目
- [ ] 点击 "New Project"
- [ ] 选择 "Deploy from GitHub repo"
- [ ] 选择 `healthcare-bbc` 仓库

### 步骤 3：添加数据库
- [ ] 点击 "New" → "Database" → "MySQL"
- [ ] 等待 MySQL 部署完成（约 1-2 分钟）
- [ ] （可选）添加 Redis： "New" → "Redis"

### 步骤 4：配置环境变量
在 Railway Dashboard → 服务 → "Variables" 中添加：

- [ ] `MYSQL_HOST` = `${{MySQL.HOST}}`
- [ ] `MYSQL_PORT` = `${{MySQL.PORT}}`
- [ ] `MYSQL_USER` = `${{MySQL.USERNAME}}`
- [ ] `MYSQL_PASSWORD` = `${{MySQL.PASSWORD}}`
- [ ] `MYSQL_DATABASE` = `${{MySQL.DATABASE}}`
- [ ] `MYSQL_ROOT_PASSWORD` = `你的根密码`
- [ ] `REDIS_HOST` = `${{Redis.HOST}}`（如使用）
- [ ] `REDIS_PORT` = `${{Redis.PORT}}`（如使用）
- [ ] `REDIS_PASSWORD` = `${{Redis.PASSWORD}}`（如使用）
- [ ] `JWT_SECRET` = `随机生成的密钥`
- [ ] `WECHAT_CORP_ID` = `你的 Corp ID`
- [ ] `WECHAT_CORP_SECRET` = `你的 Secret`
- [ ] `WECHAT_AGENT_ID` = `你的 Agent ID`
- [ ] `WECHAT_TOKEN` = `你的 Token`
- [ ] `WECHAT_ENCODING_AES_KEY` = `你的 AES Key`
- [ ] `NODE_ENV` = `production`
- [ ] `SPRING_PROFILES_ACTIVE` = `prod`
- [ ] `PORT` = `8080`

### 步骤 5：配置网络
- [ ] 进入 "Settings" → "Networking"
- [ ] 点击 "Generate Domain"
- [ ] 确认域名已生成（如 `xxx.up.railway.app`）
- [ ] 确认 "Public Networking" 已启用

### 步骤 6：配置健康检查
- [ ] 进入 "Settings" → "Healthcheck"
- [ ] Path: `/actuator/health`
- [ ] Timeout: `30s`
- [ ] Initial Delay: `60s`

### 步骤 7：部署
- [ ] 点击 "Deploy" 标签页
- [ ] 等待构建完成（约 5-10 分钟）
- [ ] 确认状态显示 "Running"

---

## 部署后验证

### 服务状态
- [ ] Railway Dashboard 显示 "Running"
- [ ] 无错误日志

### 访问测试
- [ ] 前端可访问：`https://你的域名.up.railway.app/`
- [ ] API 健康检查：`https://你的域名.up.railway.app/actuator/health`
- [ ] Swagger 文档：`https://你的域名.up.railway.app/swagger-ui.html`

### 数据库连接
- [ ] 查看日志确认数据库连接成功
- [ ] 无数据库连接错误

### 企业微信配置
- [ ] 在企业微信后台配置回调 URL
- [ ] 回调 URL: `https://你的域名.up.railway.app/wechat/callback`
- [ ] 测试消息接收

---

## 常见问题排查

### 构建失败
- [ ] 检查 GitHub 仓库是否可访问
- [ ] 查看构建日志错误信息
- [ ] 确认 Dockerfile 路径正确

### 启动失败
- [ ] 检查环境变量是否完整
- [ ] 查看运行时日志
- [ ] 确认数据库已就绪

### 无法访问
- [ ] 确认 Public Networking 已启用
- [ ] 检查域名是否正确
- [ ] 查看日志是否有绑定端口错误

### 数据库连接失败
- [ ] 确认 MySQL 插件已添加
- [ ] 检查环境变量是否正确引用
- [ ] 等待 MySQL 完全启动（约 1-2 分钟）

---

## 费用监控

- [ ] 查看 Railway Dashboard → "Usage"
- [ ] 确认未超出免费额度
- [ ] 设置消费提醒（可选）

---

## 后续优化

- [ ] 配置自定义域名
- [ ] 配置自动备份
- [ ] 设置监控告警
- [ ] 优化资源使用

---

**完成日期**: ________________

**部署人员**: ________________

**备注**:
_________________________________
_________________________________
