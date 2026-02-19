# 🚀 快速部署指南

## 方案一：Docker Compose（推荐 - 本地/任意 VPS）

### 1️⃣ 准备环境

```bash
# 安装 Docker 和 Docker Compose
curl -fsSL https://get.docker.com | bash
sudo usermod -aG docker $USER

# 验证安装
docker -v
docker compose version
```

### 2️⃣ 配置环境变量

```bash
cd deploy
cp .env.example .env

# 编辑 .env 文件，填入你的配置
vim .env
```

**必填配置**：
- `WECHAT_CORP_SECRET` - 企业微信密钥
- `WECHAT_TOKEN` - 企业微信 Token
- `WECHAT_ENCODING_AES_KEY` - 企业微信 AES 密钥
- `JWT_SECRET` - 生成随机密钥：`openssl rand -base64 32`

### 3️⃣ 启动服务

```bash
cd deploy
docker compose up -d
```

### 4️⃣ 查看状态

```bash
# 查看运行状态
docker compose ps

# 查看日志
docker compose logs -f bbc-backend

# 访问应用
# 前端：http://localhost/
# BBC 后端 API：http://localhost:8080/
# Admin 后端 API：http://localhost:8081/
```

### 5️⃣ 常用命令

```bash
# 停止服务
docker compose down

# 重启服务
docker compose restart

# 更新镜像
docker compose pull
docker compose up -d

# 查看数据库
docker exec -it healthcare-mysql mysql -u healthcare -p
```

---

## 方案二：Oracle Cloud（永久免费）

### 1️⃣ 创建实例

1. 访问 https://cloud.oracle.com/
2. 创建实例：
   - 镜像：Ubuntu 24.04 (aarch64)
   - 形状：VM.Standard.A1.Flex
   - OCPUs: 2-4, 内存：12-24GB

### 2️⃣ 执行部署脚本

```bash
# 连接到服务器后执行
cd /opt
git clone <your-repo-url> healthcare
cd healthcare/deploy

# 执行一键部署
sudo bash deploy-oracle.sh
```

### 3️⃣ 配置安全组

在 Oracle Cloud 控制台配置 VCN 安全列表：
- 入站：TCP 80, 443, 22
- 出站：允许所有

### 4️⃣ 启动应用

```bash
cd /opt/healthcare
docker compose up -d
```

---

## 方案三：Railway（最简单）

### 1️⃣ 连接 GitHub

1. 访问 https://railway.com/
2. 登录并连接 GitHub
3. 选择你的项目仓库

### 2️⃣ 配置服务

1. 点击 "New" → "GitHub Repo"
2. 选择仓库
3. Railway 自动识别 Dockerfile

### 3️⃣ 设置环境变量

在 Railway Dashboard → Variables 中添加：

```
MYSQL_ROOT_PASSWORD=xxx
MYSQL_USER=healthcare
MYSQL_PASSWORD=xxx
REDIS_PASSWORD=xxx
JWT_SECRET=xxx
WECHAT_CORP_ID=wwace533e386c63f72
WECHAT_CORP_SECRET=xxx
WECHAT_AGENT_ID=1000007
```

### 4️⃣ 添加存储

1. 点击 "New" → "Volume"
2. Mount Path: `/data`
3. Size: 5GB+

### 5️⃣ 部署完成

Railway 会自动构建和部署，访问生成的域名即可。

---

## 🔐 企业微信配置获取

### 获取企业微信参数

1. 登录 https://work.weixin.qq.com/
2. 管理后台 → 应用管理 → 自建应用
3. 查看以下信息：
   - **Corp ID**：企业微信 ID
   - **Secret**：应用密钥
   - **Agent ID**：应用 ID

### 配置回调 URL

1. 应用设置 → 接收消息设置
2. 填写：
   - URL: `https://your-domain.com/wechat/callback`
   - Token: 自定义 token
   - EncodingAESKey: 随机生成

---

## 📊 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端 | http://localhost/ | Vue3 前端 |
| BBC API | http://localhost:8080/ | Spring Boot 后端 |
| Admin API | http://localhost:8081/ | 管理后台 API |
| H2 控制台 | http://localhost:8080/h2-console | 数据库控制台（开发） |
| API 文档 | http://localhost:8080/swagger-ui.html | Swagger 文档 |

---

## 🐛 故障排查

### 容器启动失败

```bash
# 查看详细日志
docker compose logs bbc-backend

# 检查配置
docker compose config
```

### 数据库连接失败

```bash
# 检查 MySQL 是否运行
docker compose ps mysql

# 测试连接
docker exec healthcare-mysql mysql -u healthcare -p -e "SHOW DATABASES;"
```

### 端口冲突

```bash
# 查看端口占用
sudo netstat -tlnp | grep :8080

# 修改 docker-compose.yml 中的端口映射
```

---

## 📝 下一步

1. ✅ 完成部署
2. 🔧 配置企业微信参数
3. 🧪 测试 API 接口
4. 🔒 配置 HTTPS（生产环境）
5. 📊 配置监控和日志

**需要帮助？** 查看 DEPLOY.md 获取详细文档。
