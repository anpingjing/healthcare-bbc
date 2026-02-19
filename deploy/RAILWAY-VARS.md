# Railway 环境变量快速配置

## 复制以下变量到 Railway Dashboard

### 数据库配置
```
MYSQL_HOST=${{MySQL.HOST}}
MYSQL_PORT=${{MySQL.PORT}}
MYSQL_USER=${{MySQL.USERNAME}}
MYSQL_PASSWORD=${{MySQL.PASSWORD}}
MYSQL_DATABASE=healthcare_admin
MYSQL_ROOT_PASSWORD=替换为你的根密码
```

### Redis 配置（可选）
```
REDIS_HOST=${{Redis.HOST}}
REDIS_PORT=${{Redis.PORT}}
REDIS_PASSWORD=${{Redis.PASSWORD}}
```

### JWT 配置
```
JWT_SECRET=替换为随机生成的密钥
```

生成随机密钥命令：
```bash
openssl rand -base64 32
```

### 企业微信配置
```
WECHAT_CORP_ID=wwace533e386c63f72
WECHAT_CORP_SECRET=替换为你的企业密钥
WECHAT_AGENT_ID=1000007
WECHAT_TOKEN=替换为你的 Token
WECHAT_ENCODING_AES_KEY=替换为你的 AES 密钥
```

### 应用配置
```
NODE_ENV=production
SPRING_PROFILES_ACTIVE=prod
PORT=8080
SERVER_PORT=8080
LOGGING_LEVEL_ROOT=INFO
```

---

## 获取企业微信参数

1. 登录 https://work.weixin.qq.com/
2. 管理后台 → 应用管理 → 自建应用
3. 查看 Corp ID、Secret、Agent ID
4. 应用设置 → 接收消息设置 → 配置 Token 和 AES Key

---

## Railway Dashboard 操作

1. 选择服务 → Variables 标签页
2. 点击 "New Variable"
3. 逐个添加上述变量
4. 保存后自动重新部署

**注意**：`${{MySQL.HOST}}` 等语法会引用 Railway MySQL 插件的变量值。
